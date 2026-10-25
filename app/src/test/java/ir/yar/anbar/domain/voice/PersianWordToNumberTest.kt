package ir.yar.anbar.domain.voice

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PersianWordToNumberTest {

    private fun convert(phrase: String): Double? =
        PersianWordToNumber.convertOrNull(phrase.split(' ').filter { it.isNotBlank() })

    @Test
    fun `simple unit`() = assertEquals(2.0, convert("دو")!!, 0.0)

    @Test
    fun `simple teen`() = assertEquals(12.0, convert("دوازده")!!, 0.0)

    @Test
    fun `compound tens with connector`() = assertEquals(92.0, convert("نود و دو")!!, 0.0)

    @Test
    fun `hundreds compose with tens and units`() =
        assertEquals(365.0, convert("سیصد و شصت و پنج")!!, 0.0)

    @Test
    fun `scale flushes the running sum`() =
        assertEquals(250000.0, convert("دویست و پنجاه هزار")!!, 0.0)

    @Test
    fun `the full shopkeeper example`() =
        assertEquals(250532.0, convert("دویست و پنجاه هزار و پانصد و سی و دو")!!, 0.0)

    @Test
    fun `mixed scales accumulate`() =
        assertEquals(1_002_500.0, convert("یک میلیون و دو هزار و پانصد")!!, 0.0)

    @Test
    fun `bare scale means one of it`() = assertEquals(1000.0, convert("هزار")!!, 0.0)

    @Test
    fun `currency filler is ignored`() =
        assertEquals(200000.0, convert("دویست هزار تومان")!!, 0.0)

    @Test
    fun `colloquial fifteen`() = assertEquals(15.0, convert("پونزده")!!, 0.0)

    @Test
    fun `colloquial five hundred`() = assertEquals(500.0, convert("پونصد")!!, 0.0)

    @Test
    fun `colloquial billion`() = assertEquals(1_000_000_000.0, convert("همت")!!, 0.0)

    @Test
    fun `ascii digits compose with scales`() =
        assertEquals(250000.0, convert("250 هزار")!!, 0.0)

    @Test
    fun `half before a scale`() =
        assertEquals(1_500_000.0, convert("یک و نیم میلیون")!!, 0.0)

    @Test
    fun `bare half before a scale`() =
        assertEquals(500_000.0, convert("نیم میلیون")!!, 0.0)

    @Test
    fun `half without a scale`() = assertEquals(200.5, convert("دویست و نیم")!!, 0.0)

    @Test
    fun `unknown word fails the whole conversion`() = assertNull(convert("قیمت"))

    @Test
    fun `empty phrase is null`() = assertNull(convert(""))

    @Test
    fun `only fillers are null`() = assertNull(convert("و تومان"))

    @Test
    fun `integral values format without decimals`() =
        assertEquals("250532", formatSpokenNumber(250532.0))

    @Test
    fun `halves keep one decimal`() =
        assertEquals("200.5", formatSpokenNumber(200.5))
}
