package com.example.composetrainer.utils

import android.content.Context
import com.example.composetrainer.data.local.dao.CategoryDao
import com.example.composetrainer.data.local.dao.ProductDao
import com.example.composetrainer.data.local.dao.SubcategoryDao
import com.example.composetrainer.data.local.entity.CategoryEntity
import com.example.composetrainer.data.local.entity.ProductEntity
import com.example.composetrainer.data.local.entity.SubcategoryEntity
import com.example.composetrainer.utils.loadProductJson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

class ProductImporter @Inject constructor(
    private val categoryDao: CategoryDao,
    private val subcategoryDao: SubcategoryDao,
    private val productDao: ProductDao,
    @ApplicationContext private val context: Context
) {

    suspend fun importFromJson() = withContext(Dispatchers.IO) {
        val productJsonList = loadProductJson(context)

        for (productJson in productJsonList) {
            val safeCategory = productJson.category?.trim().takeIf { !it.isNullOrBlank() } ?: "بدون دسته"
            val safeSubcategory = productJson.subcategory?.trim().takeIf { !it.isNullOrBlank() } ?: "بدون زیر دسته"

            val catId = getOrInsertCategory(safeCategory)
            val subcatId = getOrInsertSubcategory(safeSubcategory, catId)

            val product = ProductEntity(
                name = productJson.name,
                barcode = productJson.barcode,
                price = null,
                image = null,
                subcategoryId = subcatId,
                date = System.currentTimeMillis(),
                stock = 0,
                costPrice = null,
                description = null,
                supplierId = null,
                unit = null,
                minStockLevel = null,
                maxStockLevel = null,
                tags = null,
                lastSoldDate = null
            )

            productDao.insertProduct(product)
        }
    }


    fun importFromJsonWithProgress(): Flow<Int> = flow {
        val productJsonList = loadProductJson(context)
        val total = productJsonList.size

        for ((index, productJson) in productJsonList.withIndex()) {
            val safeCategory = productJson.category?.trim().takeIf { !it.isNullOrBlank() } ?: "بدون دسته"
            val safeSubcategory = productJson.subcategory?.trim().takeIf { !it.isNullOrBlank() } ?: "بدون زیر دسته"

            val catId = getOrInsertCategory(safeCategory)
            val subcatId = getOrInsertSubcategory(safeSubcategory, catId)

            val product = ProductEntity(
                name = productJson.name,
                barcode = productJson.barcode,
                price = null,
                image = null,
                subcategoryId = subcatId,
                date = System.currentTimeMillis(),
                stock = 0,
                costPrice = null,
                description = null,
                supplierId = null,
                unit = null,
                minStockLevel = null,
                maxStockLevel = null,
                tags = null,
                lastSoldDate = null
            )

            productDao.insertProduct(product)

            emit(((index + 1) * 100) / total) // درصد پیشرفت
        }
    }.flowOn(Dispatchers.IO)


    private suspend fun getOrInsertCategory(name: String): Int {
        val existing = categoryDao.getByName(name)
        return if (existing != null) {
            existing.id
        } else {
            val id = categoryDao.insert(CategoryEntity(name = name)).toInt()
            if (id == -1) categoryDao.getByName(name)!!.id else id
        }
    }

    private suspend fun getOrInsertSubcategory(name: String, categoryId: Int): Int {
        val existing = subcategoryDao.getByNameAndCategoryId(name, categoryId)
        return if (existing != null) {
            existing.id
        } else {
            val id = subcategoryDao.insert(SubcategoryEntity(name = name, categoryId = categoryId)).toInt()
            if (id == -1) subcategoryDao.getByNameAndCategoryId(name, categoryId)!!.id else id
        }
    }
}

