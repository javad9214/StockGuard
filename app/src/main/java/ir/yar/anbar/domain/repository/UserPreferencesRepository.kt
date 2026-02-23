package ir.yar.anbar.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    suspend fun saveStockRunoutLimit(limit: Int)

    val stockRunoutLimit: Flow<Int>
}