package ir.yar.anbar.domain.model


import ir.yar.anbar.data.local.entity.CustomerEntity
import ir.yar.anbar.domain.model.type.Money
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.regex.Pattern

// Domain Model
data class Customer(
    val id: CustomerId,
    val name: CustomerName,
    val phone: PhoneNumber?,
    val address: Address?,
    val email: Email?,
    val note: Note?,
    val totalDebt: Money,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    // Business logic methods
    fun hasOutstandingDebt(): Boolean {
        return totalDebt.amount > 0
    }

    fun isRecentlyCreated(): Boolean {
        return createdAt.isAfter(LocalDateTime.now().minusDays(7))
    }

    fun isRecentlyUpdated(): Boolean {
        return updatedAt.isAfter(createdAt.plusMinutes(1))
    }

    fun hasContactInfo(): Boolean {
        return phone != null || email != null
    }

    fun hasCompleteProfile(): Boolean {
        return hasContactInfo() && address != null
    }

    fun getDebtStatus(): DebtStatus {
        return when (totalDebt.amount) {
            0L -> DebtStatus.NO_DEBT
            in 1..10000L -> DebtStatus.LOW_DEBT // 100.00 in cents
            else -> DebtStatus.HIGH_DEBT
        }
    }
}

// Value Objects for type safety and validation
@JvmInline
value class CustomerId(val value: Long) {
    init {
        require(value > 0) { "Customer ID must be positive" }
    }
}

@JvmInline
value class CustomerName(val value: String) {
    init {
        require(value.isNotBlank()) { "Customer name cannot be blank" }
        require(value.length <= 100) { "Customer name cannot exceed 100 characters" }
    }
}

@JvmInline
value class PhoneNumber(val value: String) {
    init {
        require(value.isNotBlank()) { "Phone number cannot be blank" }
        // Basic phone validation - adjust regex based on your needs
        val phonePattern = Pattern.compile("^[+]?[0-9\\s\\-()]+$")
        require(phonePattern.matcher(value).matches()) { "Invalid phone number format" }
    }
}

@JvmInline
value class Address(val value: String) {
    init {
        require(value.isNotBlank()) { "Address cannot be blank" }
        require(value.length <= 500) { "Address cannot exceed 500 characters" }
    }
}

@JvmInline
value class Email(val value: String) {
    init {
        require(value.isNotBlank()) { "Email cannot be blank" }
        val emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        require(emailPattern.matcher(value).matches()) { "Invalid email format" }
    }
}

@JvmInline
value class Note(val value: String) {
    init {
        require(value.isNotBlank()) { "Note cannot be blank" }
        require(value.length <= 1000) { "Note cannot exceed 1000 characters" }
    }
}

// Enums for business logic
enum class DebtStatus {
    NO_DEBT,
    LOW_DEBT,
    HIGH_DEBT
}

// Mapping Extension Functions
fun CustomerEntity.toDomain(): Customer {
    return Customer(
        id = CustomerId(id),
        name = CustomerName(name),
        phone = phone?.let { PhoneNumber(it) },
        address = address?.let { Address(it) },
        email = email?.let { Email(it) },
        note = note?.let { Note(it) },
        totalDebt = Money(totalDebt),
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

fun Customer.toEntity(): CustomerEntity {
    return CustomerEntity(
        id = id.value,
        name = name.value,
        phone = phone?.value,
        address = address?.value,
        email = email?.value,
        note = note?.value,
        totalDebt = totalDebt.amount,
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        updatedAt = updatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        isDeleted = false
    )
}

// Factory for creating new customers
object CustomerFactory {
    fun create(
        name: String,
        phone: String? = null,
        address: String? = null,
        email: String? = null,
        note: String? = null,
        totalDebt: Long = 0L
    ): Customer {
        val now = LocalDateTime.now()
        return Customer(
            id = CustomerId(0), // Will be set by database
            name = CustomerName(name),
            phone = phone?.let { PhoneNumber(it) },
            address = address?.let { Address(it) },
            email = email?.let { Email(it) },
            note = note?.let { Note(it) },
            totalDebt = Money(totalDebt),
            createdAt = now,
            updatedAt = now
        )
    }
}

// Extension functions for updating customer
fun Customer.updateName(newName: String): Customer {
    return copy(
        name = CustomerName(newName),
        updatedAt = LocalDateTime.now()
    )
}

fun Customer.updateContactInfo(
    newPhone: String? = phone?.value,
    newEmail: String? = email?.value,
    newAddress: String? = address?.value
): Customer {
    return copy(
        phone = newPhone?.let { PhoneNumber(it) },
        email = newEmail?.let { Email(it) },
        address = newAddress?.let { Address(it) },
        updatedAt = LocalDateTime.now()
    )
}

fun Customer.updateDebt(newDebtAmount: Long): Customer {
    return copy(
        totalDebt = Money(newDebtAmount),
        updatedAt = LocalDateTime.now()
    )
}

fun Customer.addDebt(additionalDebt: Long): Customer {
    return copy(
        totalDebt = Money(totalDebt.amount + additionalDebt),
        updatedAt = LocalDateTime.now()
    )
}

fun Customer.payDebt(paymentAmount: Long): Customer {
    val newAmount = maxOf(0L, totalDebt.amount - paymentAmount)
    return copy(
        totalDebt = Money(newAmount),
        updatedAt = LocalDateTime.now()
    )
}

fun Customer.updateNote(newNote: String?): Customer {
    return copy(
        note = newNote?.let { Note(it) },
        updatedAt = LocalDateTime.now()
    )
}