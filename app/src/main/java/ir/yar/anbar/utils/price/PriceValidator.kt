package ir.yar.anbar.utils.price

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object PriceValidator {

    fun formatPrice(input: String): String {
        if (input.isEmpty()) return ""

        // Remove any existing commas
        val cleanedInput = input.replace(",", "")

        // Convert to a number
        val number = cleanedInput.toLongOrNull() ?: return input

        // Create Persian number format with commas
        val symbols = DecimalFormatSymbols(Locale("fa", "IR"))
        symbols.groupingSeparator = ','
        val formatter = DecimalFormat("#,###", symbols)

        return formatter.format(number)
    }

    fun deFormatPrice(input: String): String{
        if (input.isEmpty()) return ""

        // Remove any existing commas
        val cleanedInput = input.replace(",", "")

        // Convert to a number
        val number = cleanedInput.toLongOrNull() ?: return input

        return number.toString()
    }

}