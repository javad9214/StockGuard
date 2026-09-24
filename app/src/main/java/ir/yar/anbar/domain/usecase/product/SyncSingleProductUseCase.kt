package ir.yar.anbar.domain.usecase.product

import ir.yar.anbar.domain.model.ProductSyncResult
import ir.yar.anbar.domain.repository.ProductRepository
import javax.inject.Inject

/**
 * Pushes a single product's locally pending change (create or update)
 * to the server and reports the outcome. A product with nothing pending
 * returns an empty result (already synced).
 */
class SyncSingleProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(productId: Long): ProductSyncResult {
        return productRepository.syncSingleProduct(productId)
    }
}
