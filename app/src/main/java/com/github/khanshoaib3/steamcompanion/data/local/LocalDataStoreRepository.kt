package com.github.khanshoaib3.steamcompanion.data.local

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val TAG = "LocalDataStore"

class LocalDataStoreRepository(private val dataStore: DataStore<Preferences>) {
    private companion object {
        val STEAM_CHARTS_FETCH_TIME = stringPreferencesKey("steam_charts_fetch_time")
    }

    val steamChartsFetchTime: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading STEAM_CHARTS_FETCH_TIME.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map {
            it[STEAM_CHARTS_FETCH_TIME] ?: ""
        }

    // Can be used in view models
    suspend fun steamChartsFetchTimeSnapshot(): String =
        dataStore.data.first()[STEAM_CHARTS_FETCH_TIME] ?: ""

    suspend fun saveData(steamChartsFetchTime: String) {
        dataStore.edit {
            it[STEAM_CHARTS_FETCH_TIME] = steamChartsFetchTime
        }
    }
}