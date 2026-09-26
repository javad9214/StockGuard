package ir.yar.anbar.data.repository

import ir.yar.anbar.data.local.dao.CatalogProductDao
import ir.yar.anbar.data.local.entity.CatalogProductEntity
import ir.yar.anbar.data.mapper.toBarcodeDomain
import ir.yar.anbar.data.mapper.toDomain
import ir.yar.anbar.data.remote.api.ApiServiceBarcode
import ir.yar.anbar.data.remote.dto.request.BarcodeLookupRequestDto
import ir.yar.anbar.data.remote.util.ApiResponseHandler
import ir.yar.anbar.domain.model.BarcodeProduct
import ir.yar.anbar.domain.repository.BarcodeLookupRepository
import ir.yar.anbar.domain.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class BarcodeLookupRepoImpl(
    private val apiServiceBarcode: ApiServiceBarcode,
    private val catalogProductDao: CatalogProductDao
) : BarcodeLookupRepository {

    override fun lookupBarcode(barcode: String): Flow<Resource<BarcodeProduct>> {
        return flow {
            emit(Resource.Loading())

            val cached = catalogProductDao.getCatalogProductByBarcode(barcode)

            // complete local row -> answer offline, no server round-trip
            if (cached != null && cached.suggestedPrice != null && !cached.imageUrl.isNullOrBlank()) {
                emit(Resource.Success(cached.toBarcodeDomain()))
                return@flow
            }

            // miss or incomplete row (no price/image) -> the server refreshes
            // Daryamart, backfills its own catalog and answers with catalogId
            ApiResponseHandler.handleApiResponseWithMessage(
                apiCall = { apiServiceBarcode.lookupBarcode(BarcodeLookupRequestDto(barcode)) },
                mapper = { it.toDomain() }
            ).collect { resource ->
                if (resource is Resource.Success) {
                    cacheLookupResult(barcode, resource.data, cached)
                }
                emit(resource)
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * Persists the enriched lookup result in the local catalog cache: updates
     * the incomplete row in place (keeping fields the response doesn't carry,
     * e.g. name), or inserts a fresh row keyed by the server catalog id.
     */
    private suspend fun cacheLookupResult(
        barcode: String,
        product: BarcodeProduct,
        cached: CatalogProductEntity?
    ) {
        if (cached != null) {
            catalogProductDao.updateCatalogProduct(
                cached.copy(
                    suggestedPrice = product.sellPrice ?: cached.suggestedPrice,
                    imageUrl = product.imageUrl ?: cached.imageUrl,
                    cachedAt = System.currentTimeMillis()
                )
            )
            return
        }

        if (product.catalogId != null && product.name != null) {
            catalogProductDao.insertCatalogProduct(
                CatalogProductEntity(
                    id = product.catalogId,
                    name = product.name,
                    barcode = barcode,
                    description = null,
                    brand = null,
                    manufacturer = null,
                    category = null,
                    subcategory = null,
                    imageUrl = product.imageUrl,
                    suggestedPrice = product.sellPrice,
                    unit = null,
                    tags = null,
                    status = "VERIFIED",
                    isActive = true
                )
            )
        }
    }
}
