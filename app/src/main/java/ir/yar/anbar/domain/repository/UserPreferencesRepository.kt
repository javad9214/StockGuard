package ir.yar.anbar.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    companion object {
        // Value the read flow falls back to when the preference has never
        // been saved; fresh installs see this. 0 means "only out-of-stock
        // items" in the low-stock query (stock <= limit)
        const val DEFAULT_STOCK_RUNOUT_LIMIT = 0
    }

    suspend fun saveStockRunoutLimit(limit: Int)

    val stockRunoutLimit: Flow<Int>
}