package ir.yar.anbar.domain.usecase.product


import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.model.reduceStock
import ir.yar.anbar.domain.repository.ProductRepository
import javax.inject.Inject

class DecreaseStockUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(product: Product , quantity: Int = 1){
        if (product.isInStock()) {
            repository.updateProduct(product.reduceStock(quantity))
        }
    }
}