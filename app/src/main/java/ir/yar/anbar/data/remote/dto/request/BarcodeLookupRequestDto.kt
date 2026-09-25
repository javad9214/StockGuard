package ir.yar.anbar.data.remote.dto.request

import com.google.gson.annotations.SerializedName

/** Body for POST /api/barcode/lookup. */
data class BarcodeLookupRequestDto(
    @SerializedName("barcode") val barcode: String
)
