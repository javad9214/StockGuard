package ir.yar.anbar.utils.price

import ir.yar.anbar.domain.model.type.Money
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object PriceValidator {

    fun formatPrice(money: Money): String = formatPrice(money.amount)

    // Amounts are held in cents; format what the user reads as currency units
    fun formatPrice(amountInCents: Long): String {
        val symbols = DecimalFormatSymbols(Locale("fa", "IR"))
        symbols.groupingSeparator = ','
        // Decimals only when the cents don't amount to whole units
        val pattern = if (amountInCents % 100 == 0L) "#,###" else "#,###.00"
        return DecimalFormat(pattern, symbols).format(amountInCents / 100.0)
    }
}
