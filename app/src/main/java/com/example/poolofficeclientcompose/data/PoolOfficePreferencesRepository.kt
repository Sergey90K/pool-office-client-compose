package com.example.poolofficeclientcompose.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class PoolOfficePreferencesRepository(private val dataStore: DataStore<Preferences>) {
    private companion object {
        val IS_POOL_SETTINGS = booleanPreferencesKey("is_pool_settings")
        val IS_BIOMETRIC_SETTINGS = booleanPreferencesKey("is_biometric_settings")
        const val TAG = "PoolPreferencesRepo"
    }

    suspend fun savePoolSettingsPreference(isPoolSetting: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_POOL_SETTINGS] = isPoolSetting
        }
    }

    suspend fun saveBiometricPoolSettingsPreference(isBiometricSetting: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_BIOMETRIC_SETTINGS] = isBiometricSetting
        }
    }

    val isPoolSettings: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_POOL_SETTINGS] ?: true
        }

    val isBiometricSettings: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_BIOMETRIC_SETTINGS] ?: false

        }
}