package ir.yar.anbar.domain.model

import ir.yar.anbar.data.local.entity.CustomerInvoiceSummaryEntity
import ir.yar.anbar.domain.model.type.Money
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.math.BigDecimal
import java.math.RoundingMode

// Domain Model
data class CustomerInvoiceSummary(
    val customerId: CustomerId,
    val totalInvoices: InvoiceCount,
    val totalAmount: Money,
    val totalPaid: Money,
    val totalDebt: Money,
    val lastInvoiceDate: LocalDateTime?,
    val updatedAt: LocalDateTime
) {
    // Business logic methods
    fun hasOutstandingDebt(): Boolean {
        return totalDebt.amount > 0
    }

    fun isFullyPaid(): Boolean {
        return totalDebt.amount == 0L && totalAmount.amount > 0
    }

    fun hasNoInvoices(): Boolean {
        return totalInvoices.value == 0
    }

    fun getPaymentRatio(): BigDecimal {
        if (totalAmount.amount == 0L) return BigDecimal.ZERO

        return BigDecimal(totalPaid.amount)
            .divide(BigDecimal(totalAmount.amount), 4, RoundingMode.HALF_UP)
    }

    fun getPaymentPercentage(): Int {
        return (getPaymentRatio() * BigDecimal(100)).toInt()
    }

    fun getDebtStatus(): DebtStatus {
        return when {
            totalDebt.amount == 0L -> DebtStatus.NO_DEBT
            totalDebt.amount > 0L && totalDebt.amount <= 10000L -> DebtStatus.LOW_DEBT // $100.00
            else -> DebtStatus.HIGH_DEBT
        }
    }

    fun getCustomerType(): CustomerType {
        return when {
            hasNoInvoices() -> CustomerType.NEW_CUSTOMER
            isFullyPaid() -> CustomerType.GOOD_PAYER
            getPaymentPercentage() >= 80 -> CustomerType.RELIABLE_PAYER
            getPaymentPercentage() >= 50 -> CustomerType.SLOW_PAYER
            else -> CustomerType.PROBLEMATIC_PAYER
        }
    }

    fun isActiveCustomer(): Boolean {
        return lastInvoiceDate?.isAfter(LocalDateTime.now().minusMonths(3)) == true
    }

    fun isRecentCustomer(): Boolean {
        return lastInvoiceDate?.isAfter(LocalDateTime.now().minusDays(30)) == true
    }

    fun getAverageInvoiceAmount(): Money {
        if (totalInvoices.value == 0) return Money(0L)
        return Money(totalAmount.amount / totalInvoices.value)
    }

    fun hasRecentActivity(): Boolean {
        return updatedAt.isAfter(LocalDateTime.now().minusDays(7))
    }
}

// Value Objects
@JvmInline
value class InvoiceCount(val value: Int) {
    init {
        require(value >= 0) { "Invoice count cannot be negative" }
    }
}

// Reusing Money and CustomerId from previous models
// (These would typically be in a shared domain package)

// Enums for business logic
enum class CustomerType {
    NEW_CUSTOMER,
    GOOD_PAYER,
    RELIABLE_PAYER,
    SLOW_PAYER,
    PROBLEMATIC_PAYER
}


// Mapping Extension Functions
fun CustomerInvoiceSummaryEntity.toDomain(): CustomerInvoiceSummary {
    return CustomerInvoiceSummary(
        customerId = CustomerId(customerId),
        totalInvoices = InvoiceCount(totalInvoices),
        totalAmount = Money(totalAmount),
        totalPaid = Money(totalPaid),
        totalDebt = Money(totalDebt),
        lastInvoiceDate = lastInvoiceDate?.let {
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(it),
                ZoneId.systemDefault()
            )
        },
        updatedAt = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(updatedAt),
            ZoneId.systemDefault()
        )
    )
}

fun CustomerInvoiceSummary.toEntity(): CustomerInvoiceSummaryEntity {
    return CustomerInvoiceSummaryEntity(
        customerId = customerId.value,
        totalInvoices = totalInvoices.value,
        totalAmount = totalAmount.amount,
        totalPaid = totalPaid.amount,
        totalDebt = totalDebt.amount,
        lastInvoiceDate = lastInvoiceDate?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
        updatedAt = updatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
}

// Factory for creating new summaries
object CustomerInvoiceSummaryFactory {
    fun create(customerId: Long): CustomerInvoiceSummary {
        val now = LocalDateTime.now()
        return CustomerInvoiceSummary(
            customerId = CustomerId(customerId),
            totalInvoices = InvoiceCount(0),
            totalAmount = Money(0L),
            totalPaid = Money(0L),
            totalDebt = Money(0L),
            lastInvoiceDate = null,
            updatedAt = now
        )
    }

    fun create(
        customerId: Long,
        totalInvoices: Int,
        totalAmount: Long,
        totalPaid: Long,
        lastInvoiceDate: LocalDateTime? = null
    ): CustomerInvoiceSummary {
        val now = LocalDateTime.now()
        return CustomerInvoiceSummary(
            customerId = CustomerId(customerId),
            totalInvoices = InvoiceCount(totalInvoices),
            totalAmount = Money(totalAmount),
            totalPaid = Money(totalPaid),
            totalDebt = Money(totalAmount - totalPaid),
            lastInvoiceDate = lastInvoiceDate,
            updatedAt = now
        )
    }
}

// Extension functions for updating summary
fun CustomerInvoiceSummary.addInvoice(invoiceAmount: Long): CustomerInvoiceSummary {
    return copy(
        totalInvoices = InvoiceCount(totalInvoices.value + 1),
        totalAmount = Money(totalAmount.amount + invoiceAmount),
        totalDebt = Money(totalDebt.amount + invoiceAmount),
        lastInvoiceDate = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
}

fun CustomerInvoiceSummary.recordPayment(paymentAmount: Long): CustomerInvoiceSummary {
    val newPaidAmount = totalPaid.amount + paymentAmount
    val newDebtAmount = maxOf(0L, totalAmount.amount - newPaidAmount)

    return copy(
        totalPaid = Money(newPaidAmount),
        totalDebt = Money(newDebtAmount),
        updatedAt = LocalDateTime.now()
    )
}

fun CustomerInvoiceSummary.updateLastInvoiceDate(date: LocalDateTime): CustomerInvoiceSummary {
    return copy(
        lastInvoiceDate = date,
        updatedAt = LocalDateTime.now()
    )
}

fun CustomerInvoiceSummary.recalculateDebt(): CustomerInvoiceSummary {
    val calculatedDebt = maxOf(0L, totalAmount.amount - totalPaid.amount)
    return copy(
        totalDebt = Money(calculatedDebt),
        updatedAt = LocalDateTime.now()
    )
}

// Business operation extensions
fun CustomerInvoiceSummary.getPaymentStatusText(): String {
    return when (getCustomerType()) {
        CustomerType.NEW_CUSTOMER -> "New Customer"
        CustomerType.GOOD_PAYER -> "Excellent Payment History"
        CustomerType.RELIABLE_PAYER -> "Good Payment History"
        CustomerType.SLOW_PAYER -> "Slow Payer"
        CustomerType.PROBLEMATIC_PAYER -> "Payment Issues"
    }
}

fun CustomerInvoiceSummary.shouldRequireUpfrontPayment(): Boolean {
    return getCustomerType() == CustomerType.PROBLEMATIC_PAYER ||
            (getDebtStatus() == DebtStatus.HIGH_DEBT && getPaymentPercentage() < 30)
}

fun CustomerInvoiceSummary.getRecommendedCreditLimit(): Money {
    return when (getCustomerType()) {
        CustomerType.NEW_CUSTOMER -> Money(5000L) // $50
        CustomerType.GOOD_PAYER -> Money(50000L) // $500
        CustomerType.RELIABLE_PAYER -> Money(25000L) // $250
        CustomerType.SLOW_PAYER -> Money(10000L) // $100
        CustomerType.PROBLEMATIC_PAYER -> Money(0L) // No credit
    }
}