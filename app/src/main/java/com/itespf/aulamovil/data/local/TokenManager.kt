package com.itespf.aulamovil.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session")

class TokenManager(private val context: Context) {

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_USERNAME = stringPreferencesKey("user_username")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }

    suspend fun getTokenOnce(): String? = context.dataStore.data.first()[KEY_TOKEN]

    suspend fun saveSession(token: String, name: String, username: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_USER_NAME] = name
            prefs[KEY_USER_USERNAME] = username
        }
    }

    suspend fun getUserName(): String? = context.dataStore.data.first()[KEY_USER_NAME]
    suspend fun getUserUsername(): String? = context.dataStore.data.first()[KEY_USER_USERNAME]

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}
