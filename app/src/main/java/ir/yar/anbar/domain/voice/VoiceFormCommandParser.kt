package ir.yar.anbar.domain.voice

/**
 * The AddProduct form fields a voice utterance can fill.
 */
enum class VoiceField { NAME, INITIAL_STOCK, COST_PRICE, SALE_PRICE }

/**
 * Parses a fa-IR recognition transcript into form values using the field
 * labels themselves as keywords — the same words the form shows, so there
 * is nothing new to memorize:
 *
 * «نام محصول شیر موجودی اولیه دوازده قیمت خرید دویست هزار تومان
 *   قیمت فروش دویست و پنجاه هزار»
 *
 * → name=شیر, initialStock=12, costPrice=200000, salePrice=250000
 *
 * Any subset, in any order, in a single utterance. A field's value is the
 * text between its keyword and the next keyword; numeric fields run
 * through [PersianWordToNumber], the name is kept verbatim. When the same
 * field is mentioned twice, the last mention wins (most recent intent).
 *
 * Pure Kotlin, no Android dependencies — normalization and segmentation
 * are fully unit-testable without a recognizer.
 */
object VoiceFormCommandParser {

    // Synonym phrases per field; within the sweep the longest phrase wins,
    // which is what keeps «قیمت خرید» from matching as «قیمت» + a value
    // starting with «خرید»
    private val keywordPhrases: List<Pair<List<String>, VoiceField>> = listOf(
        listOf("نام محصول", "اسم محصول") to VoiceField.NAME,
        listOf("موجودی اولیه", "موجودی", "تعداد") to VoiceField.INITIAL_STOCK,
        listOf("قیمت تمام شده", "قیمت خرید", "قیمت خریداری", "خرید") to VoiceField.COST_PRICE,
        listOf("قیمت فروش", "قیمت", "فروش") to VoiceField.SALE_PRICE,
    )

    /**
     * All keyword phrases, handed to RecognizerIntent.EXTRA_BIASING_STRINGS
     * so the recognizer favors the labels the parser segments on.
     */
    val biasingPhrases: List<String> =
        keywordPhrases.flatMap { (phrases, _) -> phrases }

    private data class KeywordMatch(val start: Int, val end: Int, val field: VoiceField)

    /**
     * Tries recognition alternates best-first and returns the first
     * transcript that yields at least one field — a mis-heard top candidate
     * usually loses only to noise, not to the runner-up.
     */
    fun parseFirstMatch(candidates: List<String>): Map<VoiceField, String> {
        for (candidate in candidates) {
            val parsed = parse(candidate)
            if (parsed.isNotEmpty()) return parsed
        }
        return emptyMap()
    }

    fun parse(transcript: String): Map<VoiceField, String> {
        val tokens = normalize(transcript).split(' ').filter { it.isNotBlank() }
        val matches = findKeywordMatches(tokens)
        if (matches.isEmpty()) return emptyMap()

        val fills = mutableMapOf<VoiceField, String>()
        matches.forEachIndexed { index, match ->
            // Each value runs up to the next keyword; the last one to the end
            val valueEnd = matches.getOrNull(index + 1)?.start ?: tokens.size
            val valueTokens = tokens.subList(match.end, valueEnd)
            val value = when (match.field) {
                VoiceField.NAME -> valueTokens.joinToString(" ").ifBlank { null }
                VoiceField.INITIAL_STOCK,
                VoiceField.COST_PRICE,
                VoiceField.SALE_PRICE ->
                    PersianWordToNumber.convertOrNull(valueTokens)
                        ?.let(::formatSpokenNumber)
            }
            if (value != null) fills[match.field] = value
        }
        return fills
    }

    /**
     * Left-to-right sweep collecting non-overlapping keyword matches. A
     * keyword appearing inside another field's value (a product named
     * «فروش ویژه») still anchors a new segment — anchoring on labels is
     * inherently ambiguous there, and cutting the value beats swallowing
     * the keyword.
     */
    private fun findKeywordMatches(tokens: List<String>): List<KeywordMatch> {
        val matches = mutableListOf<KeywordMatch>()
        var i = 0
        while (i < tokens.size) {
            val match = longestKeywordAt(tokens, i)
            if (match != null) {
                matches += match
                i = match.end
            } else {
                i++
            }
        }
        return matches
    }

    private fun longestKeywordAt(tokens: List<String>, start: Int): KeywordMatch? =
        keywordPhrases.asSequence()
            .flatMap { (phrases, field) -> phrases.asSequence().map { it to field } }
            .mapNotNull { (phrase, field) ->
                val phraseTokens = phrase.split(' ')
                val end = start + phraseTokens.size
                if (end <= tokens.size && tokens.subList(start, end) == phraseTokens) {
                    KeywordMatch(start, end, field)
                } else {
                    null
                }
            }
            .maxByOrNull { it.end - it.start }

    /**
     * Canonicalizes recognizer output before matching: Arabic yeh/kaf to
     * their Persian forms, zero-width non-joiner to a space («موجودی‌اولیه»
     * must match «موجودی اولیه»), Arabic-Indic digits to ASCII, and runs
     * of whitespace collapsed.
     */
    fun normalize(text: String): String {
        val sb = StringBuilder(text.length)
        for (c in text) {
            sb.append(
                when {
                    c == 'ي' -> 'ی'
                    c == 'ك' -> 'ک'
                    c == '\u200c' -> ' '
                    c in '٠'..'٩' -> '0' + (c - '٠')
                    c in '۰'..'۹' -> '0' + (c - '۰')
                    else -> c
                }
            )
        }
        return sb.toString().trim().replace(Regex("\\s+"), " ")
    }
}
