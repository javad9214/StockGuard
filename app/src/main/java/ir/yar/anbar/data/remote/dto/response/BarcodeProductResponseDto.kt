package ir.yar.anbar.data.remote.dto.response

import com.google.gson.annotations.SerializedName

/**
 * Server-side BarcodeProductResponseDTO: the info payload of
 * POST /api/barcode/lookup. Fields come from the catalog (or the
 * Daryamart fallback that filled it) and may be absent when the
 * upstream source has no value for them. catalogId is the server
 * catalog_products row id, used to cache the row locally.
 */
data class BarcodeProductResponseDto(
    @SerializedName("catalogId") val catalogId: Long? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("sellPrice") val sellPrice: Long? = null
)
