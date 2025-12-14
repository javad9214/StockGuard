package ir.yar.anbar.domain.model


import ir.yar.anbar.data.local.entity.InvoiceEntity
import ir.yar.anbar.domain.model.type.Money
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// Domain Model
data class Invoice (
    val id: InvoiceId,
    val prefix: InvoicePrefix,
    val invoiceNumber: InvoiceNumber,
    val invoiceDate: LocalDateTime,
    val invoiceType: InvoiceType?,
    val customerId: CustomerId?,
    val totalAmount: Money?,
    val totalProfit: Money?,
    val totalDiscount: Money,
    val status: InvoiceStatus?,
    val paymentMethod: PaymentMethod?,
    val notes: Note?,
    val synced: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    // Business logic methods
    fun getFullInvoiceNumber(): String {
        return "${prefix.value}${String.format("%04d", invoiceNumber.value)}"
    }

    fun isComplete(): Boolean {
        return totalAmount != null && status != null && customerId != null
    }

    fun isPaid(): Boolean {
        return status == InvoiceStatus.PAID
    }

    fun isPending(): Boolean {
        return status == InvoiceStatus.PENDING
    }

    fun isOverdue(): Boolean {
        return status == InvoiceStatus.OVERDUE
    }

    fun isCancelled(): Boolean {
        return status == InvoiceStatus.CANCELLED
    }

    fun hasDiscount(): Boolean {
        return totalDiscount.amount > 0
    }

    fun getDiscountPercentage(): BigDecimal {
        if (totalAmount == null || totalAmount.amount == 0L) return BigDecimal.ZERO

        return BigDecimal(totalDiscount.amount)
            .divide(BigDecimal(totalAmount.amount), 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100))
    }

    fun getProfitMargin(): BigDecimal {
        if (totalAmount == null || totalProfit == null || totalAmount.amount == 0L) {
            return BigDecimal.ZERO
        }

        return BigDecimal(totalProfit.amount)
            .divide(BigDecimal(totalAmount.amount), 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100))
    }

    fun getNetAmount(): Money {
        return totalAmount?.let { amount ->
            Money(amount.amount - totalDiscount.amount)
        } ?: Money(0L)
    }

    fun isRecentlyCreated(): Boolean {
        return createdAt.isAfter(LocalDateTime.now().minusDays(7))
    }

    fun isRecentlyUpdated(): Boolean {
        return updatedAt.isAfter(createdAt.plusMinutes(1))
    }

    fun needsSync(): Boolean {
        return !synced && isComplete()
    }

    fun getFormattedInvoiceDate(): String {
        return invoiceDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }

    fun getDaysOld(): Long {
        return java.time.Duration.between(invoiceDate, LocalDateTime.now()).toDays()
    }

    fun isOldInvoice(): Boolean {
        return getDaysOld() > 30
    }

    fun requiresFollowUp(): Boolean {
        return (isPending() || isOverdue()) && getDaysOld() > 7
    }
}

// Value Objects
@JvmInline
value class InvoiceId(val value: Long) {
    init {
        require(value >= 0) { "Invoice ID must be positive and Can be zero for new invoices" }
    }
}

@JvmInline
value class InvoicePrefix(val value: String) {
    init {
        require(value.isNotBlank()) { "Invoice prefix cannot be blank" }
        require(value.length <= 10) { "Invoice prefix cannot exceed 10 characters" }
        require(value.matches(Regex("^[A-Z]+$"))) { "Invoice prefix must contain only uppercase letters" }
    }
}

@JvmInline
value class InvoiceNumber(val value: Long) {
    init {
        require(value > 0) { "Invoice number must be positive" }
    }
}

// Enums for business logic
enum class InvoiceType(val code: String, val displayName: String) {
    SALE("S", "Sale"),
    PURCHASE("B", "Purchase"),  // "B" for Buy
    REFUND("R", "Refund");

    companion object {
        fun fromCode(code: String?): InvoiceType? {
            return entries.find { it.code == code }
        }
    }
}

enum class InvoiceStatus(val displayName: String) {
    DRAFT("Draft"),
    PENDING("Pending"),
    PAID("Paid"),
    PARTIALLY_PAID("Partially Paid"),
    OVERDUE("Overdue"),
    CANCELLED("Cancelled");

    companion object {
        fun fromString(status: String?): InvoiceStatus? {
            return entries.find { it.name == status?.uppercase() }
        }
    }
}

enum class PaymentMethod(val displayName: String) {
    CASH("Cash"),
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    BANK_TRANSFER("Bank Transfer"),
    CHECK("Check"),
    DIGITAL_WALLET("Digital Wallet"),
    CREDIT("On Credit");

    companion object {
        fun fromString(method: String?): PaymentMethod? {
            return entries.find { it.name == method?.uppercase()?.replace(" ", "_") }
        }
    }
}

// Mapping Extension Functions
fun InvoiceEntity.toDomain(): Invoice {
    return Invoice(
        id = InvoiceId(id),
        prefix = InvoicePrefix(prefix),
        invoiceNumber = InvoiceNumber(invoiceNumber),
        invoiceDate = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(invoiceDate),
            ZoneId.systemDefault()
        ),
        invoiceType = InvoiceType.fromCode(invoiceType),
        customerId = customerId?.let { CustomerId(it) },
        totalAmount = totalAmount?.let { Money(it) },
        totalProfit = totalProfit?.let { Money(it) },
        totalDiscount = Money(totalDiscount),
        status = InvoiceStatus.fromString(status),
        paymentMethod = PaymentMethod.fromString(paymentMethod),
        notes = notes?.let { Note(it) },
        synced = synced,
        createdAt = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(createdAt),
            ZoneId.systemDefault()
        ),
        updatedAt = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(updatedAt),
            ZoneId.systemDefault()
        )
    )
}

fun Invoice.toEntity(): InvoiceEntity {
    return InvoiceEntity(
        id = id.value,
        prefix = prefix.value,
        invoiceNumber = invoiceNumber.value,
        invoiceDate = invoiceDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        invoiceType = invoiceType?.code,
        customerId = customerId?.value,
        totalAmount = totalAmount?.amount,
        totalProfit = totalProfit?.amount,
        totalDiscount = totalDiscount.amount,
        status = status?.name,
        paymentMethod = paymentMethod?.name,
        notes = notes?.value,
        synced = synced,
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        updatedAt = updatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        isDeleted = false
    )
}

// Factory for creating new invoices
object InvoiceFactory {
    fun createDraft(
        invoiceId: InvoiceId = InvoiceId(0),
        customerId: Long? = null,
        invoiceType: InvoiceType = InvoiceType.SALE,
        prefix: String = "INV"
    ): Invoice {
        val now = LocalDateTime.now()
        return Invoice(
            id = invoiceId, // Will be set by database
            prefix = InvoicePrefix(prefix),
            invoiceNumber = InvoiceNumber(1), //Must Be Set Later
            invoiceDate = now,
            invoiceType = invoiceType,
            customerId = customerId?.let { CustomerId(it) },
            totalAmount = null,
            totalProfit = null,
            totalDiscount = Money(0L),
            status = InvoiceStatus.DRAFT,
            paymentMethod = null,
            notes = null,
            synced = false,
            createdAt = now,
            updatedAt = now
        )
    }

    fun createComplete(
        invoiceId: InvoiceId,
        invoiceNumber: Long,
        customerId: Long = 0L,
        totalAmount: Long,
        totalProfit: Long? = null,
        discount: Long = 0L,
        paymentMethod: PaymentMethod = PaymentMethod.CASH,
        invoiceType: InvoiceType = InvoiceType.SALE,
        prefix: String = "INV"
    ): Invoice {
        val now = LocalDateTime.now()
        return Invoice(
            id = invoiceId,
            prefix = InvoicePrefix(prefix),
            invoiceNumber = InvoiceNumber(invoiceNumber),
            invoiceDate = now,
            invoiceType = invoiceType,
            customerId = CustomerId(customerId),
            totalAmount = Money(totalAmount),
            totalProfit = totalProfit?.let { Money(it) },
            totalDiscount = Money(discount),
            status = InvoiceStatus.PAID,
            paymentMethod = paymentMethod,
            notes = null,
            synced = false,
            createdAt = now,
            updatedAt = now
        )
    }
}

// Extension functions for updating invoices
fun Invoice.markAsPaid(paymentMethod: PaymentMethod): Invoice {
    return copy(
        status = InvoiceStatus.PAID,
        paymentMethod = paymentMethod,
        updatedAt = LocalDateTime.now()
    )
}

fun Invoice.markAsOverdue(): Invoice {
    return copy(
        status = InvoiceStatus.OVERDUE,
        updatedAt = LocalDateTime.now()
    )
}

fun Invoice.updateAmounts(
    totalAmount: Long,
    totalProfit: Long? = null,
    discount: Long = this.totalDiscount.amount
): Invoice {
    return copy(
        totalAmount = Money(totalAmount),
        totalProfit = totalProfit?.let { Money(it) },
        totalDiscount = Money(discount),
        updatedAt = LocalDateTime.now()
    )
}

fun Invoice.addNote(note: String): Invoice {
    return copy(
        notes = Note(note),
        updatedAt = LocalDateTime.now()
    )
}

fun Invoice.markAsSynced(): Invoice {
    return copy(
        synced = true,
        updatedAt = LocalDateTime.now()
    )
}

fun Invoice.cancel(): Invoice {
    return copy(
        status = InvoiceStatus.CANCELLED,
        updatedAt = LocalDateTime.now()
    )
}

fun Invoice.updateInvoiceDate(newDate: LocalDateTime): Invoice {
    return copy(
        invoiceDate = newDate,
        updatedAt = LocalDateTime.now()
    )
}

fun Invoice.updateInvoiceId(newInvoiceId: InvoiceId): Invoice {
    return copy(
        id = newInvoiceId,
        updatedAt = LocalDateTime.now()
    )
}