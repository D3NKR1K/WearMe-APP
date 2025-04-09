package com.example.wearme.domain.model

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class TokenManager(context: Context) {
  private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
  private val sharedPreferences = EncryptedSharedPreferences.create(
    "secure_prefs",
    masterKeyAlias,
    context,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
  )

  companion object {
    private const val TOKEN_KEY = "jwt_token"
  }

  fun saveToken(token: String) {
    sharedPreferences.edit { putString(TOKEN_KEY, token) }
  }

  fun getToken(): String? {
    return sharedPreferences.getString(TOKEN_KEY, null)?.takeIf { it.isNotEmpty() }
  }

  fun clearToken() {
    sharedPreferences.edit { remove(TOKEN_KEY) }
  }
}
