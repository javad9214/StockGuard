package ir.yar.anbar.data.repository

import ir.yar.anbar.data.mapper.toDomain
import ir.yar.anbar.data.remote.api.ApiServiceBarcode
import ir.yar.anbar.data.remote.dto.request.BarcodeLookupRequestDto
import ir.yar.anbar.data.remote.util.ApiResponseHandler
import ir.yar.anbar.domain.model.BarcodeProduct
import ir.yar.anbar.domain.repository.BarcodeLookupRepository
import ir.yar.anbar.domain.util.Resource
import kotlinx.coroutines.flow.Flow

class BarcodeLookupRepoImpl(private val apiServiceBarcode: ApiServiceBarcode) :
    BarcodeLookupRepository {

    override fun lookupBarcode(barcode: String): Flow<Resource<BarcodeProduct>> {
        return ApiResponseHandler.handleApiResponseWithMessage(
            apiCall = { apiServiceBarcode.lookupBarcode(BarcodeLookupRequestDto(barcode)) },
            mapper = { it.toDomain() }
        )
    }
}
