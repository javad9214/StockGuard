package ir.yar.anbar.data.remote.dto.response

import com.google.gson.annotations.SerializedName

/**
 * Server-side BarcodeProductResponseDTO: the info payload of
 * POST /api/barcode/lookup. Fields come from the Daryamart catalog
 * (name, absolute imageUrl, sellPrice) and may be absent when the
 * upstream source has no value for them.
 */
data class BarcodeProductResponseDto(
    @SerializedName("name") val name: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("sellPrice") val sellPrice: Long? = null
)
