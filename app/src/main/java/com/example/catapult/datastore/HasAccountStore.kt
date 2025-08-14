package com.example.catapult.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.Preferences.Key
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HasAccountStore(
    private val dataStore: DataStore<Preferences>,
    private val key: Key<Boolean>
) {
    val hasAccountFlow: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[key] ?: false }

    suspend fun setHasAccount(value: Boolean) {
        dataStore.edit { prefs ->
            prefs[key] = value
        }
    }
}