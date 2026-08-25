package ir.yar.anbar.domain.repository

import ir.yar.anbar.domain.model.Subcategory

interface CategoryRepository {

    /**
     * Returns every available subcategory. Refreshes the local cache from the
     * server first; offline failures fall back to the cached rows.
     */
    suspend fun getSubcategories(): List<Subcategory>
}
