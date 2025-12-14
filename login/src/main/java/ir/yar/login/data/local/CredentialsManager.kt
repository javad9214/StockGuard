package ir.yar.login.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CredentialsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val sharedPreferences = EncryptedSharedPreferences.create(
        "secure_credentials",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveCredentials(phoneNumber: String, password: String) {
        sharedPreferences.edit().apply {
            putString(PHONE_KEY, phoneNumber)
            putString(PASSWORD_KEY, password)
            apply()
        }
    }

    fun getCredentials(): Pair<String, String>? {
        val phone = sharedPreferences.getString(PHONE_KEY, null)
        val password = sharedPreferences.getString(PASSWORD_KEY, null)
        return if (phone != null && password != null) {
            Pair(phone, password)
        } else null
    }

    fun clearCredentials() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        private const val PHONE_KEY = "phone_number"
        private const val PASSWORD_KEY = "password"
    }
}
