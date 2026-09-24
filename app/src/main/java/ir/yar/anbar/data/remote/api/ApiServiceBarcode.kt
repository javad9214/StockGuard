package ir.yar.anbar.data.remote.api

import com.skydoves.sandwich.ApiResponse
import ir.yar.anbar.data.remote.dto.request.BarcodeLookupRequestDto
import ir.yar.anbar.data.remote.dto.response.BarcodeProductResponseDto
import ir.yar.anbar.data.remote.dto.response.ResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServiceBarcode {

    @POST(ApiConstants.API + ApiConstants.BARCODE + "lookup")
    suspend fun lookupBarcode(
        @Body request: BarcodeLookupRequestDto
    ): ApiResponse<ResponseDto<BarcodeProductResponseDto>>
}
