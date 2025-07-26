package com.example.composetrainer.domain.model

import com.example.composetrainer.data.local.entity.InvoiceEntity
import com.example.composetrainer.data.local.entity.InvoiceProductCrossRefEntity

import java.math.BigDecimal
import java.math.RoundingMode

// Domain Model
data class InvoiceProduct(
    val invoiceId: InvoiceId,
    val productId: ProductId,
    val quantity: Quantity,
    val priceAtSale: Money,
    val costPriceAtTransaction: Money,
    val discount: Money,
    val total: Money
) {
    // Business logic methods
    fun getUnitProfitAfterDiscount(): Money {
        val netSellingPrice = priceAtSale.amount - discount.amount
        val unitProfit = netSellingPrice - costPriceAtTransaction.amount
        return Money(maxOf(0L, unitProfit))
    }

    fun getTotalProfitAfterDiscount(): Money {
        val unitProfit = getUnitProfitAfterDiscount().amount
        return Money(unitProfit * quantity.value)
    }

    fun getProfitMargin(): BigDecimal {
        val netSellingPrice = priceAtSale.amount - discount.amount
        if (netSellingPrice == 0L) return BigDecimal.ZERO

        val profit = netSellingPrice - costPriceAtTransaction.amount
        return BigDecimal(profit)
            .divide(BigDecimal(netSellingPrice), 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100))
    }

    fun getDiscountPercentage(): BigDecimal {
        if (priceAtSale.amount == 0L) return BigDecimal.ZERO

        return BigDecimal(discount.amount)
            .divide(BigDecimal(priceAtSale.amount), 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100))
    }

    fun getNetUnitPrice(): Money {
        return Money(priceAtSale.amount - discount.amount)
    }

    fun hasDiscount(): Boolean {
        return discount.amount > 0L
    }

    fun isProfitable(): Boolean {
        return getUnitProfitAfterDiscount().amount > 0L
    }

    fun isLossTransaction(): Boolean {
        val netSellingPrice = priceAtSale.amount - discount.amount
        return netSellingPrice < costPriceAtTransaction.amount
    }

    fun getMarkupPercentage(): BigDecimal {
        if (costPriceAtTransaction.amount == 0L) return BigDecimal.ZERO

        val netSellingPrice = priceAtSale.amount - discount.amount
        val markup = netSellingPrice - costPriceAtTransaction.amount

        return BigDecimal(markup)
            .divide(BigDecimal(costPriceAtTransaction.amount), 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100))
    }

    fun isHighQuantity(): Boolean {
        return quantity.value >= 10
    }

    fun isBulkOrder(): Boolean {
        return quantity.value >= 50
    }

    fun getTransactionType(): TransactionType {
        return when {
            isLossTransaction() -> TransactionType.LOSS
            !isProfitable() -> TransactionType.BREAK_EVEN
            getProfitMargin() >= BigDecimal(30) -> TransactionType.HIGH_MARGIN
            getProfitMargin() >= BigDecimal(15) -> TransactionType.GOOD_MARGIN
            else -> TransactionType.LOW_MARGIN
        }
    }

    fun shouldApplyBulkDiscount(): Boolean {
        return isBulkOrder() && !hasDiscount()
    }

    fun calculateTotal(): Money {
        val netUnitPrice = getNetUnitPrice().amount
        return Money(netUnitPrice * quantity.value)
    }

    fun validateTotalCalculation(): Boolean {
        return total.amount == calculateTotal().amount
    }
}

// Value Objects
@JvmInline
value class ProductId(val value: Long) {
    init {
        require(value > 0) { "Product ID must be positive" }
    }
}

@JvmInline
value class Quantity(val value: Int) {
    init {
        require(value > 0) { "Quantity must be positive" }
        require(value <= 10000) { "Quantity cannot exceed 10,000 units" }
    }
}

// Enums for business logic
enum class TransactionType {
    LOSS,
    BREAK_EVEN,
    LOW_MARGIN,
    GOOD_MARGIN,
    HIGH_MARGIN
}

// Mapping Extension Functions
fun InvoiceProductCrossRefEntity.toDomain(): InvoiceProduct {
    return InvoiceProduct(
        invoiceId = InvoiceId(invoiceId),
        productId = ProductId(productId),
        quantity = Quantity(quantity),
        priceAtSale = Money(priceAtSale),
        costPriceAtTransaction = Money(costPriceAtTransaction),
        discount = Money(discount),
        total = Money(total)
    )
}

fun InvoiceProduct.toEntity(): InvoiceProductCrossRefEntity {
    return InvoiceProductCrossRefEntity(
        invoiceId = invoiceId.value,
        productId = productId.value,
        quantity = quantity.value,
        priceAtSale = priceAtSale.amount,
        costPriceAtTransaction = costPriceAtTransaction.amount,
        discount = discount.amount,
        total = total.amount
    )
}

// Factory for creating invoice products
object InvoiceProductFactory {
    fun create(
        invoiceId: Long,
        productId: Long,
        quantity: Int,
        priceAtSale: Long,
        costPriceAtTransaction: Long,
        discount: Long = 0L
    ): InvoiceProduct {
        val netUnitPrice = priceAtSale - discount
        val calculatedTotal = netUnitPrice * quantity

        return InvoiceProduct(
            invoiceId = InvoiceId(invoiceId),
            productId = ProductId(productId),
            quantity = Quantity(quantity),
            priceAtSale = Money(priceAtSale),
            costPriceAtTransaction = Money(costPriceAtTransaction),
            discount = Money(discount),
            total = Money(calculatedTotal)
        )
    }

    fun createWithAutoDiscount(
        invoiceId: Long,
        productId: Long,
        quantity: Int,
        priceAtSale: Long,
        costPriceAtTransaction: Long,
        bulkDiscountThreshold: Int = 50,
        bulkDiscountPercentage: Int = 5
    ): InvoiceProduct {
        val discount = if (quantity >= bulkDiscountThreshold) {
            (priceAtSale * bulkDiscountPercentage) / 100
        } else {
            0L
        }

        return create(
            invoiceId = invoiceId,
            productId = productId,
            quantity = quantity,
            priceAtSale = priceAtSale,
            costPriceAtTransaction = costPriceAtTransaction,
            discount = discount
        )
    }
}

// Extension functions for updating invoice products
fun InvoiceProduct.updateQuantity(newQuantity: Int): InvoiceProduct {
    val quantity = Quantity(newQuantity)
    val newTotal = getNetUnitPrice().amount * quantity.value

    return copy(
        quantity = quantity,
        total = Money(newTotal)
    )
}

fun InvoiceProduct.updatePriceAtSale(newPrice: Long): InvoiceProduct {
    val newTotal = (newPrice - discount.amount) * quantity.value

    return copy(
        priceAtSale = Money(newPrice),
        total = Money(newTotal)
    )
}

fun InvoiceProduct.applyDiscount(discountAmount: Long): InvoiceProduct {
    val newDiscount = Money(discountAmount)
    val newTotal = (priceAtSale.amount - discountAmount) * quantity.value

    return copy(
        discount = newDiscount,
        total = Money(newTotal)
    )
}

fun InvoiceProduct.applyPercentageDiscount(percentage: Int): InvoiceProduct {
    require(percentage in 0..100) { "Discount percentage must be between 0 and 100" }

    val discountAmount = (priceAtSale.amount * percentage) / 100
    return applyDiscount(discountAmount)
}

fun InvoiceProduct.removeDiscount(): InvoiceProduct {
    return copy(
        discount = Money(0L),
        total = Money(priceAtSale.amount * quantity.value)
    )
}

fun InvoiceProduct.updateCostPrice(newCostPrice: Long): InvoiceProduct {
    return copy(
        costPriceAtTransaction = Money(newCostPrice)
    )
}

// Business analysis extensions
fun InvoiceProduct.getTransactionSummary(): String {
    return buildString {
        append("Qty: ${quantity.value}, ")
        append("Unit Price: $${priceAtSale.toDisplayAmount()}, ")
        if (hasDiscount()) {
            append("Discount: $${discount.toDisplayAmount()}, ")
        }
        append("Total: $${total.toDisplayAmount()}, ")
        append("Profit: $${getTotalProfitAfterDiscount().toDisplayAmount()}, ")
        append("Margin: ${getProfitMargin().setScale(1, RoundingMode.HALF_UP)}%")
    }
}

fun InvoiceProduct.getOptimizationSuggestion(): String {
    return when (getTransactionType()) {
        TransactionType.LOSS -> "Warning: This transaction results in a loss. Consider increasing price."
        TransactionType.BREAK_EVEN -> "This transaction breaks even. Consider slight price increase."
        TransactionType.LOW_MARGIN -> "Low profit margin. Consider optimizing cost or price."
        TransactionType.GOOD_MARGIN -> "Good profit margin. Transaction is healthy."
        TransactionType.HIGH_MARGIN -> "Excellent profit margin. Consider competitive pricing."
    }
}

// Collection extensions for multiple invoice products
fun List<InvoiceProduct>.getTotalAmount(): Money {
    return Money(sumOf { it.total.amount })
}

fun List<InvoiceProduct>.getTotalProfit(): Money {
    return Money(sumOf { it.getTotalProfitAfterDiscount().amount })
}

fun List<InvoiceProduct>.getTotalDiscount(): Money {
    return Money(sumOf { it.discount.amount * it.quantity.value })
}

fun List<InvoiceProduct>.getAverageProfitMargin(): BigDecimal {
    if (isEmpty()) return BigDecimal.ZERO

    val totalMargin = fold(BigDecimal.ZERO) { acc, item ->
        acc.add(item.getProfitMargin())
    }

    return totalMargin.divide(BigDecimal(size), 2, RoundingMode.HALF_UP)
}