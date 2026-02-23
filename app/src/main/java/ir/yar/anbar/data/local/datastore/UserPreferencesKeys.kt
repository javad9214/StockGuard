package ir.yar.anbar.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore


val Context.userPreferencesDataStore by preferencesDataStore(name = "user_settings")


object UserPreferencesKeys {
    val STOCK_RUNOUT_ALERT_LIMIT = intPreferencesKey("stock_runout_alert_limit")
}