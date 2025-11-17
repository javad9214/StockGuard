package com.example.composetrainer.domain.model

import com.example.composetrainer.domain.model.type.Money
import java.time.LocalDateTime



// Domain Model
data class InvoiceWithProducts(
    val invoice: Invoice,
    val invoiceProducts: List<InvoiceProduct>,
    val products: List<Product>
) {
    // Computed properties for convenience
    val invoiceId: InvoiceId get() = invoice.id
    val invoiceNumber: InvoiceNumber get() = invoice.invoiceNumber
    val totalProductsCount: Int get() = invoiceProducts.size
    val totalQuantity: Int get() = invoiceProducts.sumOf { it.quantity.value }

    // Validation
    fun isValid(): Boolean = invoiceProducts.all { it.invoiceId == invoice.id }

    
    
    companion object {
        // Factory method for creating empty InvoiceWithProducts
        fun empty(): InvoiceWithProducts {
            return InvoiceWithProducts(
                invoice = InvoiceFactory.createDraft(),
                invoiceProducts = emptyList(),
                products = emptyList()
            )
        }

        // Factory method for creating with default invoice values
        fun createDefault(
            invoiceId: InvoiceId = InvoiceId(0),
            prefix: InvoicePrefix = InvoicePrefix("INV"),
            invoiceNumber: InvoiceNumber = InvoiceNumber(1),
            customerId: CustomerId? = null,
            invoiceType: InvoiceType = InvoiceType.SALE,
            paymentMethod: PaymentMethod? = null,
            notes: Note? = null
        ): InvoiceWithProducts {
            val now = LocalDateTime.now()
            val defaultInvoice = Invoice(
                id = invoiceId,
                prefix = prefix,
                invoiceNumber = invoiceNumber,
                invoiceDate = now,
                invoiceType = invoiceType,
                customerId = customerId,
                totalAmount = Money(0),
                totalProfit = Money(0),
                totalDiscount = Money(0),
                status = InvoiceStatus.DRAFT, // Assuming you have a DRAFT status
                paymentMethod = paymentMethod,
                notes = notes,
                synced = false,
                createdAt = now,
                updatedAt = now
            )

            return InvoiceWithProducts(
                invoice = defaultInvoice,
                invoiceProducts = emptyList(),
                products = emptyList()
            )
        }

        // Factory method for creating from existing data
        fun create(
            invoice: Invoice,
            products: List<InvoiceProduct>,
            allProducts: List<Product> = emptyList() // newly added for product mapping
        ): InvoiceWithProducts {
            // Filter products to ensure they belong to this invoice
            val validProducts = products.filter { it.invoiceId == invoice.id }
            // Derive matching full Product objects
            val invoiceProductIds = validProducts.map { it.productId }.toSet()
            val validDomainProducts = allProducts.filter { it.id in invoiceProductIds }
            return InvoiceWithProducts(
                invoice = invoice,
                invoiceProducts = validProducts,
                products = validDomainProducts
            ).syncInvoiceTotals() // Automatically sync totals
        }

        // Factory method for creating with auto-calculated totals
        fun createWithCalculatedTotals(
            invoiceId: InvoiceId = InvoiceId(0),
            prefix: InvoicePrefix = InvoicePrefix("INV"),
            invoiceNumber: InvoiceNumber,
            invoiceDate: LocalDateTime = LocalDateTime.now(),
            invoiceProducts: List<InvoiceProduct>,
            domainProducts: List<Product> = emptyList(),
            invoiceType: InvoiceType? = null,
            customerId: CustomerId? = null,
            status: InvoiceStatus? = null,
            paymentMethod: PaymentMethod? = null,
            notes: Note? = null
        ): InvoiceWithProducts {
            val now = LocalDateTime.now()
            val validProducts = invoiceProducts.filter { it.invoiceId == invoiceId }

            // Calculate totals from products
            val totalAmount = validProducts.fold(Money(0)) { acc, product -> acc + product.total }
            val totalProfit = validProducts.fold(Money(0)) { acc, product ->
                val profitPerUnit = product.priceAtSale - product.costPriceAtTransaction
                val totalProfit = Money(profitPerUnit.amount * product.quantity.value)
                acc + totalProfit
            }
            val totalDiscount = validProducts.fold(Money(0)) { acc, product -> acc + product.discount }

            val invoice = Invoice(
                id = invoiceId,
                prefix = prefix,
                invoiceNumber = invoiceNumber,
                invoiceDate = invoiceDate,
                invoiceType = invoiceType,
                customerId = customerId,
                totalAmount = totalAmount,
                totalProfit = totalProfit,
                totalDiscount = totalDiscount,
                status = status,
                paymentMethod = paymentMethod,
                notes = notes,
                synced = false,
                createdAt = now,
                updatedAt = now
            )

            val invoiceProductIds = validProducts.map { it.productId }.toSet()
            val onlyProducts = domainProducts.filter { it.id in invoiceProductIds }

            return InvoiceWithProducts(
                invoice = invoice,
                invoiceProducts = validProducts,
                products = onlyProducts
            )
        }
    }

}

// Extension functions for Invoice
fun Invoice.withProducts(
    products: List<InvoiceProduct>,
    fullProducts: List<Product> = emptyList()
): InvoiceWithProducts {
    val invoiceProductIds = products.map { it.productId }.toSet()
    val domainProducts = fullProducts.filter { it.id in invoiceProductIds }
    return InvoiceWithProducts(
        invoice = this,
        invoiceProducts = products.filter { it.invoiceId == this.id },
        products = domainProducts
    )
}

fun Invoice.withoutProducts(): InvoiceWithProducts {
    return InvoiceWithProducts(
        invoice = this,
        invoiceProducts = emptyList(),
        products = emptyList()
    )
}

// Extension functions for InvoiceWithProducts
fun InvoiceWithProducts.addProduct(
    product: InvoiceProduct,
    domainProduct: Product?
): InvoiceWithProducts {
    require(product.invoiceId == this.invoiceId) {
        "Product invoiceId must match invoice id"
    }
    val updatedDomainProducts =
        if (domainProduct != null && !products.any { it.id == domainProduct.id }) products + domainProduct else products
    return this.copy(
        invoiceProducts = invoiceProducts + product,
        products = updatedDomainProducts
    )
}

fun InvoiceWithProducts.removeProduct(productId: ProductId): InvoiceWithProducts {
    return this.copy(
        invoiceProducts = invoiceProducts.filterNot { it.productId == productId },
        products = products.filterNot { it.id == productId }
    )
}

fun InvoiceWithProducts.updateProduct(
    productId: ProductId,
    updater: (InvoiceProduct) -> InvoiceProduct,
    domainProductUpdater: ((Product) -> Product)? = null // Optional domain product update
): InvoiceWithProducts {
    return this.copy(
        invoiceProducts = invoiceProducts.map { product ->
            if (product.productId == productId) {
                updater(product).also { updated ->
                    require(updated.invoiceId == this.invoiceId) {
                        "Updated product invoiceId must match invoice id"
                    }
                }
            } else {
                product
            }
        },
        products = products.map {
            if (it.id == productId && domainProductUpdater != null) domainProductUpdater(it) else it
        }
    )
}

fun InvoiceWithProducts.getProduct(productId: ProductId): InvoiceProduct? {
    return invoiceProducts.find { it.productId == productId }
}

fun InvoiceWithProducts.hasProduct(productId: ProductId): Boolean {
    return invoiceProducts.any { it.productId == productId }
}

fun InvoiceWithProducts.hasProducts(): Boolean {
    return invoiceProducts.isNotEmpty()
}

// Extension function to auto-create Invoice from invoiceProducts data and update InvoiceWithProducts
fun InvoiceWithProducts.autoCreateInvoice(): InvoiceWithProducts {
    // Extract invoiceId and invoiceNumber from invoiceProducts
    val extractedInvoiceId = invoiceProducts.firstOrNull()?.invoiceId
        ?: throw IllegalStateException("Cannot create invoice: no invoice products available")

    // For invoice number, we'll use the invoiceId value as invoice number
    // You can modify this logic based on your business requirements
    val extractedInvoiceNumber = InvoiceNumber(extractedInvoiceId.value)

    val now = LocalDateTime.now()

    // Calculate totals from invoice products
    val calculatedTotalAmount = invoiceProducts.fold(Money(0)) { acc, product ->
        acc + product.total
    }

    val calculatedTotalProfit = invoiceProducts.fold(Money(0)) { acc, product ->
        val profitPerUnit = product.priceAtSale - product.costPriceAtTransaction
        val totalProfit = Money(profitPerUnit.amount * product.quantity.value)
        acc + totalProfit
    }

    val calculatedTotalDiscount = invoiceProducts.fold(Money(0)) { acc, product ->
        acc + product.discount
    }

    // Create new invoice with extracted and calculated data
    val newInvoice = Invoice(
        id = extractedInvoiceId,
        prefix = InvoicePrefix("INV"), // Default prefix, you can make this configurable
        invoiceNumber = extractedInvoiceNumber,
        invoiceDate = now,
        invoiceType = InvoiceType.SALE, // Default type, you can make this configurable
        customerId = null, // Will need to be set separately if needed
        totalAmount = if (invoiceProducts.isNotEmpty()) calculatedTotalAmount else null,
        totalProfit = if (invoiceProducts.isNotEmpty()) calculatedTotalProfit else null,
        totalDiscount = calculatedTotalDiscount,
        status = InvoiceStatus.DRAFT, // Default status
        paymentMethod = null, // Will need to be set separately if needed
        notes = null,
        synced = false,
        createdAt = now,
        updatedAt = now
    )

    // Return updated InvoiceWithProducts with the new invoice
    return this.copy(
        invoice = newInvoice
    )
}

// Alternative version with configurable parameters
fun InvoiceWithProducts.autoCreateInvoice(
    prefix: InvoicePrefix = InvoicePrefix("INV"),
    invoiceType: InvoiceType = InvoiceType.SALE,
    customerId: CustomerId? = null,
    status: InvoiceStatus = InvoiceStatus.DRAFT,
    paymentMethod: PaymentMethod? = null,
    notes: Note? = null
): InvoiceWithProducts {
    // Extract invoiceId from invoiceProducts
    val extractedInvoiceId = invoiceProducts.firstOrNull()?.invoiceId
        ?: throw IllegalStateException("Cannot create invoice: no invoice products available")

    // Use invoiceId value as invoice number (you can modify this logic)
    val extractedInvoiceNumber = InvoiceNumber(extractedInvoiceId.value)

    val now = LocalDateTime.now()

    // Calculate totals from invoice products
    val calculatedTotalAmount = invoiceProducts.fold(Money(0)) { acc, product ->
        acc + product.total
    }

    val calculatedTotalProfit = invoiceProducts.fold(Money(0)) { acc, product ->
        val profitPerUnit = product.priceAtSale - product.costPriceAtTransaction
        val totalProfit = Money(profitPerUnit.amount * product.quantity.value)
        acc + totalProfit
    }

    val calculatedTotalDiscount = invoiceProducts.fold(Money(0)) { acc, product ->
        acc + product.discount
    }

    // Create new invoice with extracted and calculated data
    val newInvoice = Invoice(
        id = extractedInvoiceId,
        prefix = prefix,
        invoiceNumber = extractedInvoiceNumber,
        invoiceDate = now,
        invoiceType = invoiceType,
        customerId = customerId,
        totalAmount = if (invoiceProducts.isNotEmpty()) calculatedTotalAmount else null,
        totalProfit = if (invoiceProducts.isNotEmpty()) calculatedTotalProfit else null,
        totalDiscount = calculatedTotalDiscount,
        status = status,
        paymentMethod = paymentMethod,
        notes = notes,
        synced = false,
        createdAt = now,
        updatedAt = now
    )

    // Return updated InvoiceWithProducts with the new invoice
    return this.copy(
        invoice = newInvoice
    )
}

// Alternative version that uses the existing invoice as a template but recalculates totals
fun InvoiceWithProducts.autoCreateInvoiceFromTemplate(): Invoice {
    val now = LocalDateTime.now()

    // Calculate totals from invoice products
    val calculatedTotalAmount = invoiceProducts.fold(Money(0)) { acc, product ->
        acc + product.total
    }

    val calculatedTotalProfit = invoiceProducts.fold(Money(0)) { acc, product ->
        val profitPerUnit = product.priceAtSale - product.costPriceAtTransaction
        val totalProfit = Money(profitPerUnit.amount * product.quantity.value)
        acc + totalProfit
    }

    val calculatedTotalDiscount = invoiceProducts.fold(Money(0)) { acc, product ->
        acc + product.discount
    }

    return invoice.copy(
        totalAmount = if (invoiceProducts.isNotEmpty()) calculatedTotalAmount else null,
        totalProfit = if (invoiceProducts.isNotEmpty()) calculatedTotalProfit else null,
        totalDiscount = calculatedTotalDiscount,
        updatedAt = now
    )
}

// Calculation extension functions
fun InvoiceWithProducts.calculateTotalAmount(): Money {
    return invoiceProducts.fold(Money(0)) { acc, product -> acc + product.total }
}

fun InvoiceWithProducts.calculateTotalProfit(): Money {
    return invoiceProducts.fold(Money(0)) { acc, product ->
        val profitPerUnit = product.priceAtSale - product.costPriceAtTransaction
        val totalProfit = Money(profitPerUnit.amount * product.quantity.value)
        acc + totalProfit
    }
}

fun InvoiceWithProducts.calculateTotalDiscount(): Money {
    return invoiceProducts.fold(Money(0)) { acc, product -> acc + product.discount }
}

fun InvoiceWithProducts.calculateTotalCost(): Money {
    return invoiceProducts.fold(Money(0)) { acc, product ->
        val totalCost = Money(product.costPriceAtTransaction.amount * product.quantity.value)
        acc + totalCost
    }
}

// Update invoice totals based on products
fun InvoiceWithProducts.syncInvoiceTotals(): InvoiceWithProducts {
    val calculatedTotal = calculateTotalAmount()
    val calculatedProfit = calculateTotalProfit()
    val calculatedDiscount = calculateTotalDiscount()

    return this.copy(
        invoice = invoice.copy(
            totalAmount = calculatedTotal,
            totalProfit = calculatedProfit,
            totalDiscount = calculatedDiscount,
            updatedAt = LocalDateTime.now()
        )
    )
}

// Update invoice ID and all related invoice products' invoice IDs
fun InvoiceWithProducts.updateInvoiceId(newInvoiceId: InvoiceId): InvoiceWithProducts {
    return this.copy(
        invoice = invoice.copy(
            id = newInvoiceId,
            updatedAt = LocalDateTime.now()
        ),
        invoiceProducts = invoiceProducts.map { invoiceProduct ->
            invoiceProduct.copy(invoiceId = newInvoiceId)
        }
    )
}

// Mapping functions for different use cases
fun InvoiceWithProducts.toInvoiceOnly(): Invoice = invoice

fun InvoiceWithProducts.toProductsOnly(): List<InvoiceProduct> = invoiceProducts

fun InvoiceWithProducts.toInvoiceSummary(): InvoiceSummary {
    return InvoiceSummary(
        id = invoice.id,
        invoiceNumber = invoice.invoiceNumber,
        invoiceDate = invoice.invoiceDate,
        customerName = null, // You might need to join with customer data
        totalAmount = invoice.totalAmount ?: calculateTotalAmount(),
        status = invoice.status,
        invoiceType = invoice.invoiceType,
        productsCount = invoiceProducts.size
    )
}

// Helper data class for summary view
data class InvoiceSummary(
    val id: InvoiceId,
    val invoiceNumber: InvoiceNumber,
    val invoiceDate: LocalDateTime,
    val customerName: String?,
    val totalAmount: Money,
    val status: InvoiceStatus?,
    val invoiceType: InvoiceType?,
    val productsCount: Int
)

// Collection extension functions
fun List<InvoiceWithProducts>.getTotalRevenue(): Money {
    return fold(Money(0)) { acc, invoiceWithProducts ->
        acc + invoiceWithProducts.calculateTotalAmount()
    }
}

fun List<InvoiceWithProducts>.getTotalProfit(): Money {
    return fold(Money(0)) { acc, invoiceWithProducts ->
        acc + invoiceWithProducts.calculateTotalProfit()
    }
}

fun List<InvoiceWithProducts>.filterByStatus(status: InvoiceStatus): List<InvoiceWithProducts> {
    return filter { it.invoice.status == status }
}

fun List<InvoiceWithProducts>.filterByDateRange(
    startDate: LocalDateTime,
    endDate: LocalDateTime
): List<InvoiceWithProducts> {
    return filter {
        it.invoice.invoiceDate.isAfter(startDate) && it.invoice.invoiceDate.isBefore(endDate)
    }
}

// Money class extension functions for calculations
operator fun Money.plus(other: Money): Money = Money(this.amount + other.amount)
operator fun Money.minus(other: Money): Money = Money(this.amount - other.amount)
operator fun Money.times(multiplier: Int): Money = Money(this.amount * multiplier)
operator fun Money.times(multiplier: Long): Money = Money(this.amount * multiplier)