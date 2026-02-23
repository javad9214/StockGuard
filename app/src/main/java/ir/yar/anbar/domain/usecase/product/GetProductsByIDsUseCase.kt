package ir.yar.anbar.domain.usecase.product

import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductsByIDsUseCase @Inject constructor(
    private val productRepository: ProductRepository){

    suspend operator fun invoke(idProducts: List<Long>): List<Product> {
        return productRepository.getProductsByIds(idProducts)
    }
}