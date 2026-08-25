package ir.yar.anbar.domain.usecase.product

import ir.yar.anbar.domain.model.ProductSyncResult
import ir.yar.anbar.domain.repository.ProductRepository
import javax.inject.Inject

/**
 * Pushes every locally pending product change (creates, updates, deletes)
 * to the server and reports how many succeeded.
 */
class SyncAllProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(): ProductSyncResult {
        return productRepository.syncAllProducts()
    }
}
