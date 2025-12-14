package ir.yar.anbar.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import ir.yar.anbar.data.local.datastore.UserPreferencesKeys
import ir.yar.anbar.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    override suspend fun saveStockRunoutLimit(limit: Int) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.STOCK_RUNOUT_ALERT_LIMIT] = limit
        }
    }

    override val stockRunoutLimit: Flow<Int> =
        dataStore.data.map { preferences ->
            preferences[UserPreferencesKeys.STOCK_RUNOUT_ALERT_LIMIT] ?: 0
        }
}