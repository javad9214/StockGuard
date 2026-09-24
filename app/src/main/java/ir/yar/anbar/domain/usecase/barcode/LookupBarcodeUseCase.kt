package ir.yar.anbar.domain.usecase.barcode

import ir.yar.anbar.domain.model.BarcodeProduct
import ir.yar.anbar.domain.repository.BarcodeLookupRepository
import ir.yar.anbar.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LookupBarcodeUseCase @Inject constructor(
    private val repository: BarcodeLookupRepository
) {
    operator fun invoke(barcode: String): Flow<Resource<BarcodeProduct>> {
        return repository.lookupBarcode(barcode.trim())
    }
}
