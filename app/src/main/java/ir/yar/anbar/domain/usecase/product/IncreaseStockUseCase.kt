package ir.yar.anbar.domain.usecase.product


import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.model.addStock
import ir.yar.anbar.domain.repository.ProductRepository
import javax.inject.Inject

class IncreaseStockUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(product: Product, quantity: Int = 1){
        repository.updateProduct(product.addStock(quantity))
    }
}