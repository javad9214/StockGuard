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
        // Parses user-supplied text (a decimal display amount, e.g. "100" or
        // "12.5") into cents; returns null when the input is blank,
        // non-numeric, or zero/negative so callers reject the input instead
        // of silently falling back to a default
        fun parsePositiveOrNull(input: String): Money? {
            val displayAmount = input.trim().toDoubleOrNull() ?: return null
            val cents = Math.round(displayAmount * 100)
            return if (cents > 0) Money(cents) else null
        }
    }
}