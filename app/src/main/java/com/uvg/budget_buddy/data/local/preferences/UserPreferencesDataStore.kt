package com.uvg.budget_buddy.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesDataStore(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        private val SIMULATE_ERRORS = booleanPreferencesKey("simulate_errors")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    val isDarkMode: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { it[IS_DARK_MODE] ?: false }

    val simulateErrors: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { it[SIMULATE_ERRORS] ?: false }

    val userId: Flow<String?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { it[USER_ID] }

    val userEmail: Flow<String?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { it[USER_EMAIL] }

    val isLoggedIn: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { it[IS_LOGGED_IN] ?: false }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[IS_DARK_MODE] = enabled }
    }

    suspend fun setSimulateErrors(enabled: Boolean) {
        dataStore.edit { it[SIMULATE_ERRORS] = enabled }
    }

    suspend fun saveUserData(userId: String, email: String) {
        dataStore.edit {
            it[USER_ID] = userId
            it[USER_EMAIL] = email
            it[IS_LOGGED_IN] = true
        }
    }

    suspend fun clearUserData() {
        dataStore.edit {
            it.remove(USER_ID)
            it.remove(USER_EMAIL)
            it[IS_LOGGED_IN] = false
        }
    }
}