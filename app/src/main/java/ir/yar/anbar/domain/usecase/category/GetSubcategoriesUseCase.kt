package ir.yar.anbar.domain.usecase.category

import ir.yar.anbar.domain.model.Subcategory
import ir.yar.anbar.domain.repository.CategoryRepository
import javax.inject.Inject

class GetSubcategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(): List<Subcategory> {
        return categoryRepository.getSubcategories()
    }
}
