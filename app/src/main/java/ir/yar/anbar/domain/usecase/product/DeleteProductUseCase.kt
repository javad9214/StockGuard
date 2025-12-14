package ir.yar.anbar.domain.usecase.product

import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.repository.ProductRepository
import javax.inject.Inject

class DeleteProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(product: Product) {
        repository.deleteProduct(product)
    }
}