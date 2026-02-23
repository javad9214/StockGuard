package ir.yar.anbar.domain.model.type

@JvmInline
value class Money(val amount: Long) { // Amount in cents to avoid floating point issues
    init {
        require(amount >= 0) { "Money amount cannot be negative" }
    }

    fun toDisplayAmount(): Double = amount / 100.0

    fun isZero(): Boolean = amount == 0L

    fun isPositive(): Boolean = amount > 0L
}