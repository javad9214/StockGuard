package ir.yar.anbar.domain.usecase.product

import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.repository.ProductRepository
import ir.yar.anbar.ui.viewmodels.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllProductUseCase @Inject constructor(
    private val repository: ProductRepository
)  {
    operator fun invoke(sortOrder: SortOrder): Flow<List<Product>> {
        return repository.getAllProducts().map { products ->
            when (sortOrder) {
                SortOrder.ASCENDING -> products.sortedBy { it.date }
                SortOrder.DESCENDING -> products.sortedByDescending { it.date }
            }
        }
    }
}