package ir.yar.anbar.domain.model

/**
 * Units a product's stock can be counted in. Mirrors the server's Unit enum
 * entry-for-entry: the enum [name] is the only value accepted on the wire and
 * stored in the local DB (strict enum contract — no aliases, no faName
 * parsing); [faName] is display-only.
 */
enum class UnitOfMeasure(val faName: String) {
    // Count
    PIECE("عدد"),
    PACK("بسته"),
    BOX("جعبه"),
    CARTON("کارتن"),
    DOZEN("دوجین"),
    PAIR("جفت"),
    SET("ست"),
    ROLL("رول"),
    SHEET("برگ"),
    BAG("کیسه"),
    BOTTLE("بطری"),
    CAN("قوطی"),
    JAR("شیشه"),
    TRAY("سینی"),
    PALLET("پالت"),
    BUNDLE("دسته"),

    // Weight
    GRAM("گرم"),
    KILOGRAM("کیلوگرم"),
    TON("تن"),
    MITHQAL("مثقال"),
    SEER("سیر"),

    // Volume
    MILLILITER("میلی‌لیتر"),
    LITER("لیتر"),
    CUBIC_METER("متر مکعب"),

    // Length
    MILLIMETER("میلی‌متر"),
    CENTIMETER("سانتی‌متر"),
    METER("متر"),
    KILOMETER("کیلومتر"),

    // Area
    SQUARE_METER("متر مربع");

    companion object {
        /** Exact-name lookup only — legacy or unknown values resolve to null. */
        fun fromName(value: String?): UnitOfMeasure? =
            value?.let { runCatching { valueOf(it) }.getOrNull() }
    }
}
