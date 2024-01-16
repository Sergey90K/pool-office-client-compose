package com.example.poolofficeclientcompose

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.poolofficeclientcompose.data.AppContainer
import com.example.poolofficeclientcompose.data.DefaultAppContainer
import com.example.poolofficeclientcompose.data.PoolOfficePreferencesRepository

private const val POOL_PREFERENCE_NAME = "pool_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = POOL_PREFERENCE_NAME
)

class PoolOfficeApplication :  Application(){
    lateinit var container: AppContainer
    lateinit var poolOfficePreferencesRepository: PoolOfficePreferencesRepository
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
        poolOfficePreferencesRepository = PoolOfficePreferencesRepository(dataStore)
    }
}