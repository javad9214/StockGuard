package ir.yar.anbar.domain.voice

/**
 * Converts spoken Persian number phrases into numeric values, e.g.
 * «دویست و پنجاه هزار و پانصد و سی و دو» → 250532.
 *
 * Returns [Double] because spoken prices can legitimately end in «و نیم»
 * («هشتصد و نیم»); integral results format cleanly via [formatSpokenNumber].
 *
 * Strict by design: any token that is not a number word, a scale, the
 * «و» connector, a currency filler («تومان»), or a plain digit run makes
 * the whole conversion fail (null) — silently skipping unknown words would
 * turn a mis-heard phrase into a confidently wrong number.
 */
object PersianWordToNumber {

    private val units = mapOf(
        "یک" to 1.0, "دو" to 2.0, "سه" to 3.0, "چهار" to 4.0, "پنج" to 5.0,
        "شش" to 6.0, "شیش" to 6.0, "هفت" to 7.0, "هشت" to 8.0, "نه" to 9.0,
    )

    // 10–19 are single words in Persian — no «ده و پنج» composition exists
    private val teens = mapOf(
        "ده" to 10.0, "یازده" to 11.0, "دوازده" to 12.0, "سیزده" to 13.0,
        "چهارده" to 14.0, "پانزده" to 15.0, "پونزده" to 15.0,
        "شانزده" to 16.0, "شونزده" to 16.0, "هفده" to 17.0,
        "هجده" to 18.0, "نوزده" to 19.0,
    )

    private val tens = mapOf(
        "بیست" to 20.0, "سی" to 30.0, "چهل" to 40.0, "پنجاه" to 50.0,
        "شصت" to 60.0, "هفتاد" to 70.0, "هشتاد" to 80.0, "نود" to 90.0,
    )

    private val hundreds = mapOf(
        "صد" to 100.0, "یکصد" to 100.0, "دویست" to 200.0, "سیصد" to 300.0,
        "چهارصد" to 400.0, "پانصد" to 500.0, "پونصد" to 500.0,
        "ششصد" to 600.0, "هفتصد" to 700.0, "هشتصد" to 800.0, "نهصد" to 900.0,
    )

    private val scales = mapOf(
        "هزار" to 1_000.0,
        "میلیون" to 1_000_000.0,
        "میلیارد" to 1_000_000_000.0,
        "همت" to 1_000_000_000.0, // colloquial میلیارد
    )

    // Spoken alongside numbers but semantically neutral: the additive
    // connector «و», currency units, and polite filler
    private val fillers = setOf("و", "تومان", "تومن", "ریال", "عدد")

    private fun valueOf(word: String): Double? =
        units[word] ?: teens[word] ?: tens[word] ?: hundreds[word] ?: scales[word]

    /**
     * @param tokens whitespace-split tokens of the (normalized) phrase.
     * @return the numeric value, or null if any token is unrecognized or
     *         no number word appears at all.
     */
    fun convertOrNull(tokens: List<String>): Double? {
        var current = 0.0          // sum since the last scale word
        var total = 0.0            // sum of completed scale groups
        var sawNumber = false
        var pendingHalf = false    // «یک و نیم میلیون» — the ½ applies to the next scale

        for (token in tokens) {
            when {
                token in fillers -> Unit

                // Recognizers often emit the digits directly («250 هزار»).
                // ASCII only: Char.isDigit() would admit Persian digits
                // («۲۵۰») that toDouble() then throws on; the parser's
                // normalize() has already mapped them to ASCII anyway
                token.all { it in '0'..'9' } -> {
                    current += token.toDouble()
                    sawNumber = true
                }

                token == "نیم" -> {
                    // «و نیم» after a scale-less group adds 0.5 to it
                    // (دویست و نیم → 200.5); before a scale it is resolved
                    // when the scale token arrives below
                    pendingHalf = true
                    sawNumber = true
                }

                token in scales -> {
                    val scale = scales.getValue(token)
                    // «هزار» alone means 1×هزار; «یک و نیم میلیون» is
                    // (1 + 0.5)×میلیون; «نیم میلیون» is 0.5×میلیون
                    val multiplier = when {
                        current > 0.0 && pendingHalf -> current + 0.5
                        current > 0.0 -> current
                        pendingHalf -> 0.5
                        else -> 1.0
                    }
                    total += multiplier * scale
                    current = 0.0
                    pendingHalf = false
                    sawNumber = true
                }

                else -> valueOf(token)?.let { value ->
                    current += value
                    sawNumber = true
                } ?: return null
            }
        }

        if (!sawNumber) return null
        if (pendingHalf) current += 0.5
        return total + current
    }
}

/**
 * Formats a spoken-number result for the form's numeric text fields:
 * integral values lose the trailing «.0» («250000» not «250000.0»),
 * halves keep one decimal («250.5») which [ir.yar.anbar.domain.model.type.Money]
 * accepts for prices.
 */
fun formatSpokenNumber(value: Double): String =
    if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
