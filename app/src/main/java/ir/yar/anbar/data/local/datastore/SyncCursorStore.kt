package ir.yar.anbar.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Persisted pull-sync cursors. The invoice cursor holds the serverTime from
 * the last fully-merged pull; the next pull asks for everything changed since.
 */
class SyncCursorStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private val invoiceCursorKey = longPreferencesKey("invoice_sync_cursor")

    suspend fun invoiceSyncCursor(): Long =
        dataStore.data.first()[invoiceCursorKey] ?: 0L

    suspend fun saveInvoiceSyncCursor(serverTime: Long) {
        dataStore.edit { preferences ->
            preferences[invoiceCursorKey] = serverTime
        }
    }
}