package ir.yar.anbar.domain.repository

import ir.yar.anbar.domain.model.UnitOfMeasure
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    companion object {
        // Value the read flow falls back to when the preference has never
        // been saved; fresh installs see this. 0 means "only out-of-stock
        // items" in the low-stock query (stock <= limit)
        const val DEFAULT_STOCK_RUNOUT_LIMIT = 0

        // Unit pre-selected on the add-product form when the user hasn't
        // chosen one. Must be a valid UnitOfMeasure enum name — the strict
        // enum contract forbids anything else on the wire
        val DEFAULT_UNIT: String = UnitOfMeasure.PIECE.name
    }

    suspend fun saveStockRunoutLimit(limit: Int)

    val stockRunoutLimit: Flow<Int>

    suspend fun saveDefaultUnit(unit: String)

    val defaultUnit: Flow<String>
}
