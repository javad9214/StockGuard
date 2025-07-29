package com.example.composetrainer.domain.model

import com.example.composetrainer.domain.model.type.Money
import java.time.LocalDateTime



// Domain Model
data class InvoiceWithProducts(
    val invoice: Invoice,
    val products: List<InvoiceProduct>
) {
    // Computed properties for convenience
    val invoiceId: InvoiceId get() = invoice.id
    val totalProductsCount: Int get() = products.size
    val totalQuantity: Int get() = products.sumOf { it.quantity.value }

    // Validation
    fun isValid(): Boolean = products.all { it.invoiceId == invoice.id }

    companion object {
        // Factory method for creating empty InvoiceWithProducts
        fun empty(invoice: Invoice): InvoiceWithProducts {
            return InvoiceWithProducts(
                invoice = invoice,
                products = emptyList()
            )
        }

        // Factory method for creating with default invoice values
        fun createDefault(
            invoiceId: InvoiceId,
            prefix: InvoicePrefix,
            invoiceNumber: InvoiceNumber,
            customerId: CustomerId? = null,
            invoiceType: InvoiceType? = null,
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
                products = emptyList()
            )
        }

        // Factory method for creating from existing data
        fun create(
            invoice: Invoice,
            products: List<InvoiceProduct>
        ): InvoiceWithProducts {
            // Filter products to ensure they belong to this invoice
            val validProducts = products.filter { it.invoiceId == invoice.id }

            return InvoiceWithProducts(
                invoice = invoice,
                products = validProducts
            ).syncInvoiceTotals() // Automatically sync totals
        }

        // Factory method for creating with auto-calculated totals
        fun createWithCalculatedTotals(
            invoiceId: InvoiceId,
            prefix: InvoicePrefix,
            invoiceNumber: InvoiceNumber,
            invoiceDate: LocalDateTime,
            products: List<InvoiceProduct>,
            invoiceType: InvoiceType? = null,
            customerId: CustomerId? = null,
            status: InvoiceStatus? = null,
            paymentMethod: PaymentMethod? = null,
            notes: Note? = null
        ): InvoiceWithProducts {
            val now = LocalDateTime.now()
            val validProducts = products.filter { it.invoiceId == invoiceId }

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

            return InvoiceWithProducts(
                invoice = invoice,
                products = validProducts
            )
        }
    }

}

// Extension functions for Invoice
fun Invoice.withProducts(products: List<InvoiceProduct>): InvoiceWithProducts {
    return InvoiceWithProducts(
        invoice = this,
        products = products.filter { it.invoiceId == this.id }
    )
}

fun Invoice.withoutProducts(): InvoiceWithProducts {
    return InvoiceWithProducts(
        invoice = this,
        products = emptyList()
    )
}

// Extension functions for InvoiceWithProducts
fun InvoiceWithProducts.addProduct(product: InvoiceProduct): InvoiceWithProducts {
    require(product.invoiceId == this.invoiceId) {
        "Product invoiceId must match invoice id"
    }
    return this.copy(products = products + product)
}

fun InvoiceWithProducts.removeProduct(productId: ProductId): InvoiceWithProducts {
    return this.copy(products = products.filterNot { it.productId == productId })
}

fun InvoiceWithProducts.updateProduct(
    productId: ProductId,
    updater: (InvoiceProduct) -> InvoiceProduct
): InvoiceWithProducts {
    return this.copy(
        products = products.map { product ->
            if (product.productId == productId) {
                updater(product).also { updated ->
                    require(updated.invoiceId == this.invoiceId) {
                        "Updated product invoiceId must match invoice id"
                    }
                }
            } else {
                product
            }
        }
    )
}

fun InvoiceWithProducts.getProduct(productId: ProductId): InvoiceProduct? {
    return products.find { it.productId == productId }
}

fun InvoiceWithProducts.hasProduct(productId: ProductId): Boolean {
    return products.any { it.productId == productId }
}

// Calculation extension functions
fun InvoiceWithProducts.calculateTotalAmount(): Money {
    return products.fold(Money(0)) { acc, product -> acc + product.total }
}

fun InvoiceWithProducts.calculateTotalProfit(): Money {
    return products.fold(Money(0)) { acc, product ->
        val profitPerUnit = product.priceAtSale - product.costPriceAtTransaction
        val totalProfit = Money(profitPerUnit.amount * product.quantity.value)
        acc + totalProfit
    }
}

fun InvoiceWithProducts.calculateTotalDiscount(): Money {
    return products.fold(Money(0)) { acc, product -> acc + product.discount }
}

fun InvoiceWithProducts.calculateTotalCost(): Money {
    return products.fold(Money(0)) { acc, product ->
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

// Mapping functions for different use cases
fun InvoiceWithProducts.toInvoiceOnly(): Invoice = invoice

fun InvoiceWithProducts.toProductsOnly(): List<InvoiceProduct> = products

fun InvoiceWithProducts.toInvoiceSummary(): InvoiceSummary {
    return InvoiceSummary(
        id = invoice.id,
        invoiceNumber = invoice.invoiceNumber,
        invoiceDate = invoice.invoiceDate,
        customerName = null, // You might need to join with customer data
        totalAmount = invoice.totalAmount ?: calculateTotalAmount(),
        status = invoice.status,
        productsCount = products.size
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