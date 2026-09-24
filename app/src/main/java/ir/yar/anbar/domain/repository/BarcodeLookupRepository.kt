package ir.yar.anbar.domain.repository

import ir.yar.anbar.domain.model.BarcodeProduct
import ir.yar.anbar.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface BarcodeLookupRepository {

    /**
     * Resolves a scanned barcode against the server's Daryamart lookup
     * endpoint. Emits [Resource.Error] with the server's fa message
     * (HTTP 404) when no product matches.
     */
    fun lookupBarcode(barcode: String): Flow<Resource<BarcodeProduct>>
}
