package ir.yar.anbar.utils.price

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat

fun ThousandSeparatorTransformation(): VisualTransformation {
    return VisualTransformation { text ->
        val original = text.text
        if (original.isEmpty()) {
            return@VisualTransformation TransformedText(AnnotatedString(""), OffsetMapping.Identity)
        }

        // remove commas for parsing
        val number = original.replace(",", "")
        val formatted = try {
            DecimalFormat("#,###").format(number.toLong())
        } catch (e: NumberFormatException) {
            original // fallback if not a valid number
        }

        val out = AnnotatedString(formatted)

        // Handle cursor offset mapping
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // move cursor considering extra commas
                val before = number.take(offset)
                val formattedBefore = try {
                    DecimalFormat("#,###").format(before.toLong())
                } catch (_: Exception) {
                    before
                }
                return formattedBefore.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                // remove commas again
                val trimmed = formatted.take(offset).replace(",", "")
                return trimmed.length.coerceAtMost(original.length)
            }
        }

        TransformedText(out, offsetMapping)
    }
}
