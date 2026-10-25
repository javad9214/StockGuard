package ir.yar.anbar.domain.voice

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VoiceFormCommandParserTest {

    @Test
    fun `full utterance fills all four fields`() {
        val fills = VoiceFormCommandParser.parse(
            "نام محصول شیر پرچرب موجودی اولیه دوازده " +
                "قیمت خرید دویست هزار تومان قیمت فروش دویست و پنجاه هزار"
        )
        assertEquals("شیر پرچرب", fills[VoiceField.NAME])
        assertEquals("12", fills[VoiceField.INITIAL_STOCK])
        assertEquals("200000", fills[VoiceField.COST_PRICE])
        assertEquals("250000", fills[VoiceField.SALE_PRICE])
    }

    @Test
    fun `fields can come in any order and subset`() {
        val fills = VoiceFormCommandParser.parse(
            "قیمت فروش پنجاه هزار نام محصول شیر"
        )
        assertEquals("شیر", fills[VoiceField.NAME])
        assertEquals("50000", fills[VoiceField.SALE_PRICE])
        assertNull(fills[VoiceField.COST_PRICE])
    }

    @Test
    fun `cost and sale both anchor despite sharing قیمت`() {
        val fills = VoiceFormCommandParser.parse(
            "قیمت خرید هزار قیمت فروش دو هزار"
        )
        assertEquals("1000", fills[VoiceField.COST_PRICE])
        assertEquals("2000", fills[VoiceField.SALE_PRICE])
    }

    @Test
    fun `synonym anchors work`() {
        assertEquals(
            "شیر",
            VoiceFormCommandParser.parse("اسم محصول شیر")[VoiceField.NAME]
        )
        assertEquals(
            "30000",
            VoiceFormCommandParser.parse("قیمت تمام شده سی هزار")[VoiceField.COST_PRICE]
        )
    }

    @Test
    fun `zero-width non-joiner inside a keyword is normalized away`() {
        val fills = VoiceFormCommandParser.parse("موجودی\u200cاولیه دوازده")
        assertEquals("12", fills[VoiceField.INITIAL_STOCK])
    }

    @Test
    fun `arabic yeh in a number word is normalized`() {
        // «سي» with Arabic yeh instead of Persian «سی»
        val fills = VoiceFormCommandParser.parse("قیمت فروش سي هزار")
        assertEquals("30000", fills[VoiceField.SALE_PRICE])
    }

    @Test
    fun `persian digits in the transcript are normalized`() {
        val fills = VoiceFormCommandParser.parse("قیمت خرید ۲۵۰ هزار")
        assertEquals("250000", fills[VoiceField.COST_PRICE])
    }

    @Test
    fun `last mention of a repeated field wins`() {
        val fills = VoiceFormCommandParser.parse("نام محصول شیر نام محصول پنیر")
        assertEquals("پنیر", fills[VoiceField.NAME])
    }

    @Test
    fun `non-numeric value skips the field`() {
        assertTrue(
            VoiceFormCommandParser.parse("قیمت خرید گران").isEmpty()
        )
    }

    @Test
    fun `transcript without keywords yields nothing`() {
        assertTrue(
            VoiceFormCommandParser.parse("سلام خوبی").isEmpty()
        )
    }

    @Test
    fun `blank name value is skipped`() {
        val fills = VoiceFormCommandParser.parse("نام محصول قیمت خرید هزار")
        assertEquals("1000", fills[VoiceField.COST_PRICE])
        assertNull(fills[VoiceField.NAME])
    }

    @Test
    fun `parseFirstMatch falls through to an alternate`() {
        val fills = VoiceFormCommandParser.parseFirstMatch(
            listOf("یک جمله بی‌ربط", "نام محصول شیر")
        )
        assertEquals("شیر", fills[VoiceField.NAME])
    }

    @Test
    fun `parseFirstMatch with no usable candidates is empty`() {
        assertTrue(
            VoiceFormCommandParser.parseFirstMatch(listOf("سلام", "خوبی")).isEmpty()
        )
    }

    @Test
    fun `biasing phrases cover the primary labels`() {
        val phrases = VoiceFormCommandParser.biasingPhrases
        assertTrue(phrases.contains("نام محصول"))
        assertTrue(phrases.contains("موجودی اولیه"))
        assertTrue(phrases.contains("قیمت خرید"))
        assertTrue(phrases.contains("قیمت فروش"))
    }
}
