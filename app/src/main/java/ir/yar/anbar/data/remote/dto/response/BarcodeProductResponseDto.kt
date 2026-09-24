package ir.yar.anbar.data.remote.dto.response

/**
 * Server-side BarcodeProductResponseDTO: the info payload of
 * POST /api/barcode/lookup. Fields come from the Daryamart catalog
 * (name, absolute imageUrl, sellPrice) and may be absent when the
 * upstream source has no value for them.
 */
data class BarcodeProductResponseDto(
    val name: String? = null,
    val imageUrl: String? = null,
    val sellPrice: Long? = null
)
