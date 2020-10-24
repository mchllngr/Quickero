package de.mchllngr.quickero.repository.application

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

typealias PackageName = String

@Singleton
class ApplicationsRepository @Inject constructor(
    private val packageManager: PackageManager,
    private val dataStore: DataStore<Preferences>
) {

    val applications: Flow<List<Application>> = dataStore.data
        .catch {
            if (it is IOException) {
                Timber.w(it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preference ->
            val firstStart = preference.getFirstStart()
            if (firstStart) {
                setFirstStart(false)
                setDummyPackageNames()
                return@map emptyList()
            }

            preference.getPackageNames().toApplications()
        }

    suspend fun setApplications(applications: List<Application>) {
        setPackageNames(applications.map { it.packageName })
    }

    private suspend fun setDummyPackageNames() {
        val installedApplications = getInstalledApplications()
        val dummyEntries = DUMMY_PACKAGE_NAMES.asSequence()
            .filterNot { dummy -> installedApplications.any { dummy == it.packageName } }
            .take(DUMMY_ENTRIES_MAX_COUNT)
            .toList()
        setPackageNames(dummyEntries)
    }

    private fun getInstalledApplications(): List<ApplicationInfo> = packageManager.getInstalledApplications(0)

    private fun List<PackageName>.toApplications() = mapNotNull {
        if (!it.isLaunchable()) return@mapNotNull null

        return@mapNotNull try {
            val info = packageManager.getApplicationInfo(it, 0)
            val icon = packageManager.getApplicationIcon(it)
            val name = packageManager.getApplicationLabel(info)
            Application(it, icon, name)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    private fun PackageName.isLaunchable() = isNotBlank() && packageManager.getLaunchIntentForPackage(this) != null

    private fun Preferences.getFirstStart() = get(KEY_FIRST_START) ?: true

    private suspend fun setFirstStart(firstStart: Boolean) {
        dataStore.edit { preference ->
            preference[KEY_FIRST_START] = firstStart
        }
    }

    private fun Preferences.getPackageNames(): List<PackageName> = get(KEY_PACKAGE_NAMES).orEmpty().split(PACKAGE_NAMES_SEPARATOR)

    private suspend fun setPackageNames(applications: List<PackageName>) {
        dataStore.edit { preference ->
            preference[KEY_PACKAGE_NAMES] = applications.joinToString(separator = PACKAGE_NAMES_SEPARATOR)
        }
    }

    companion object {

        private val KEY_FIRST_START = preferencesKey<Boolean>("applications_first_start")
        private val KEY_PACKAGE_NAMES = preferencesKey<PackageName>("applications_package_names")

        private const val PACKAGE_NAMES_SEPARATOR = ";###;"

        private const val DUMMY_ENTRIES_MAX_COUNT = 5
        private val DUMMY_PACKAGE_NAMES: List<PackageName> = listOf(
            "com.whatsapp",
            "com.facebook.orca",
            "com.google.android.talk",
            "com.twitter.android",
            "org.thoughtcrime.securesms",
            "com.facebook.katana",
            "com.snapchat.android",
            "com.instagram.android",
            "com.google.android.youtube",
            "ch.threema.app",
            "com.google.android.apps.photos",
            "com.netflix.mediaclient",
            "com.spotify.music",
            "com.amazon.mShop.android.shopping",
            "com.skype.raider",
            "com.ebay.mobile",
            "com.shazam.android",
            "org.telegram.messenger",
            "com.google.android.play.games",
            "com.google.android.apps.tachyon",
            "com.tinder",
            "com.paypal.android.p2pmobile",
            "com.google.android.music",
            "com.google.android.calendar",
            "com.dropbox.android",
            "com.amazon.kindle",
            "com.google.android.apps.books"
        )
    }
}
