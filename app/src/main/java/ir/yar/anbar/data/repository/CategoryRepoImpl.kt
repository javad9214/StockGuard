package ir.yar.anbar.data.repository

import com.skydoves.sandwich.ApiResponse
import ir.yar.anbar.data.local.dao.CategoryDao
import ir.yar.anbar.data.local.dao.SubcategoryDao
import ir.yar.anbar.data.local.entity.CategoryEntity
import ir.yar.anbar.data.local.entity.SubcategoryEntity
import ir.yar.anbar.data.remote.api.ApiServiceCategory
import ir.yar.anbar.data.remote.dto.response.CategoryWithSubcategoriesDto
import ir.yar.anbar.data.mapper.toDomain
import ir.yar.anbar.domain.model.Subcategory
import ir.yar.anbar.domain.repository.CategoryRepository
import javax.inject.Inject

class CategoryRepoImpl @Inject constructor(
    private val apiServiceCategory: ApiServiceCategory,
    private val categoryDao: CategoryDao,
    private val subcategoryDao: SubcategoryDao
) : CategoryRepository {

    override suspend fun getSubcategories(): List<Subcategory> {
        refreshFromServer()
        return subcategoryDao.getAll().toDomain()
    }

    private suspend fun refreshFromServer() {
        val categories = try {
            (apiServiceCategory.getCategories() as? ApiResponse.Success)?.data?.data
        } catch (e: Exception) {
            null // offline or malformed response — serve the local cache instead
        } ?: return

        categoryDao.insertAll(categories.map { it.toCategoryEntity() })
        subcategoryDao.insertAll(categories.flatMap { it.toSubcategoryEntities() })
    }

    private fun CategoryWithSubcategoriesDto.toCategoryEntity() = CategoryEntity(
        id = id,
        name = name,
        icon = icon
    )

    private fun CategoryWithSubcategoriesDto.toSubcategoryEntities() =
        subcategories.map { subcategory ->
            SubcategoryEntity(
                id = subcategory.id,
                name = subcategory.name,
                categoryId = id,
                icon = subcategory.icon
            )
        }
}
