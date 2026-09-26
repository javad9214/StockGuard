package ir.yar.anbar.domain.model

/**
 * Product found by scanning a barcode, resolved by the server against its
 * catalog (with a Daryamart fallback). All fields are optional: the upstream
 * source may not have a price or image for the item. catalogId is the server
 * catalog row id, used by the data layer for local caching; UI code ignores it.
 */
data class BarcodeProduct(
    val catalogId: Long? = null,
    val name: String? = null,
    val imageUrl: String? = null,
    val sellPrice: Long? = null
)
