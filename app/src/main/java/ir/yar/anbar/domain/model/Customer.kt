package ir.yar.anbar.domain.model

// Value Objects for type safety and validation
@JvmInline
value class CustomerId(val value: Long) {
    init {
        require(value > 0) { "Customer ID must be positive" }
    }
}

@JvmInline
value class Note(val value: String) {
    init {
        require(value.isNotBlank()) { "Note cannot be blank" }
        require(value.length <= 1000) { "Note cannot exceed 1000 characters" }
    }
}
