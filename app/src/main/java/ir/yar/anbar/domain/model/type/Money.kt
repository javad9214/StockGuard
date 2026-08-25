package ir.yar.anbar.domain.model.type

@JvmInline
value class Money(val amount: Long) { // Amount in cents to avoid floating point issues
    init {
        require(amount >= 0) { "Money amount cannot be negative" }
    }

    fun toDisplayAmount(): Double = amount / 100.0

    fun isZero(): Boolean = amount == 0L

    fun isPositive(): Boolean = amount > 0L

    companion object {
        // Parses user-supplied text into a positive amount; returns null when
        // the input is blank, non-numeric, or zero/negative so callers reject
        // the input instead of silently falling back to a default
        fun parsePositiveOrNull(input: String): Money? {
            val amount = input.trim().toLongOrNull() ?: return null
            return if (amount > 0) Money(amount) else null
        }
    }
}