package de.mchllngr.quickero.di

import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val DATA_STORE_NAME = "settings_pref"

    private val Context.dataStore by preferencesDataStore(DATA_STORE_NAME)

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> = context.dataStore

    @Singleton
    @Provides
    fun providePackageManager(@ApplicationContext context: Context): PackageManager = context.packageManager

    @Singleton
    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}
