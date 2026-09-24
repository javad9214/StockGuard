package ir.yar.anbar.domain.model

/**
 * Product found by scanning a barcode, resolved by the server against the
 * Daryamart catalog. All fields are optional: the upstream source may not
 * have a price or image for the item.
 */
data class BarcodeProduct(
    val name: String? = null,
    val imageUrl: String? = null,
    val sellPrice: Long? = null
)
