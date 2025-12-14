package ir.yar.anbar.domain.usecase.product

import ir.yar.anbar.domain.repository.ProductRepository
import javax.inject.Inject

class CheckProductStockUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(
        productId: Long,
        requestedQty: Int
    ): AddProductResult {
        val product = productRepository.getProductById(productId)
            ?: return AddProductResult.Error("NO Product found")
        val availableQty = product.stock

        return if (requestedQty > availableQty.value) {
            AddProductResult.Error(
                "Requested $requestedQty, but only $availableQty available"
            )
        } else {
            AddProductResult.Success
        }
    }
}


sealed class AddProductResult {
    data object Success : AddProductResult()
    data class Error(val reason: String) : AddProductResult()
}