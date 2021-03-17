package de.mchllngr.quickero.repository.notification

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    val enabled: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                Timber.w(it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { it.getEnabled() }

    private fun Preferences.getEnabled() = get(KEY_ENABLED) ?: true

    suspend fun setEnabled(enabled: Boolean) {
        dataStore.edit { preference ->
            preference[KEY_ENABLED] = enabled
        }
    }

    companion object {

        private val KEY_ENABLED = booleanPreferencesKey("notification_enabled")
    }
}
