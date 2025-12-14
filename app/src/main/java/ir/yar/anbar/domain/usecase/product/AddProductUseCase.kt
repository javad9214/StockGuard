package ir.yar.anbar.domain.usecase.product

import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.repository.ProductRepository
import javax.inject.Inject

class AddProductUseCase @Inject constructor(private val productRepository: ProductRepository) {
    suspend operator fun invoke(product: Product){
        productRepository.addProduct(product)
    }

    companion object{
        const val TAG = "AddProductUseCase"
    }

}