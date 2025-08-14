package com.example.catapult.di

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.core.DataStore
import com.example.catapult.datastore.HasAccountStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "catapult_prefs")

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    private val HAS_ACCOUNT_KEY = booleanPreferencesKey("has_account")

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.dataStore

    @Provides
    @Singleton
    fun provideHasAccountStore(
        dataStore: DataStore<Preferences>
    ): HasAccountStore = HasAccountStore(dataStore, HAS_ACCOUNT_KEY)
}
