package de.mchllngr.quickero.repository.application

import android.content.pm.PackageManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.core.preferencesSetKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
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
        .emptyOnIOException()
        .map { preferences ->
            preferences.getPackageNames()
                .filter { it.isLaunchable() }
                .toApplications()
                .take(PACKAGE_NAMES_MAX_COUNT)
        }

    val applicationsMaxReached = applications.map { it.size < PACKAGE_NAMES_MAX_COUNT }

    fun getInstalledApplications() = flowOf(getInstalledPackageNames())
        .combine(applications) { installedPackageNames, savedApplications ->
            installedPackageNames to savedApplications.map { it.packageName }
        }
        .map { (installedPackageNames, savedPackageNames) ->
            installedPackageNames
                .filterNot { it in savedPackageNames }
                .toApplications()
                .sortedWith { a, b -> a.name.toString().compareTo(b.name.toString(), true) }
        }

    suspend fun addApplication(
        packageName: PackageName,
        position: Int
    ) {
        updatePackageNames { add(if (position > size) size else position, packageName) }
    }

    suspend fun moveApplication(
        fromPosition: Int,
        toPosition: Int
    ) {
        if (fromPosition == toPosition) return

        updatePackageNames {
            val packageName = removeAt(fromPosition)
            add(toPosition, packageName)
        }
    }

    suspend fun removeApplication(position: Int): PackageName? {
        var removedPackageName: PackageName? = null
        updatePackageNames {
            removedPackageName = removeAt(position)
        }
        return removedPackageName
    }

    suspend fun removeInvalidApplications() {
        updatePackageNames {
            filter { it.isLaunchable() }
        }
    }

    suspend fun setDummyPackageNamesOnFirstStart() {
        val preferences = dataStore.data.emptyOnIOException().firstOrNull()
        if (preferences?.getFirstStart() == false) return

        setFirstStart(false)

        val installedPackageNames = getInstalledPackageNames()

        val dummyEntries = DUMMY_PACKAGE_NAMES.asSequence()
            .shuffled()
            .filter { it in installedPackageNames }
            .take(DUMMY_ENTRIES_MAX_COUNT)
            .toMutableList()

        if (dummyEntries.size < DUMMY_ENTRIES_MAX_COUNT) {
            dummyEntries += (installedPackageNames - dummyEntries)
                .shuffled()
                .take(DUMMY_ENTRIES_MAX_COUNT - dummyEntries.size)
            dummyEntries.shuffle()
        }

        setPackageNames(dummyEntries)
    }

    // region helper

    private suspend fun updatePackageNames(block: suspend MutableList<PackageName>.() -> Unit) {
        val preferences = dataStore.data.emptyOnIOException().firstOrNull() ?: return
        val packageNames = preferences.getPackageNames().toMutableList()
        packageNames.block()
        setPackageNames(packageNames)
    }

    private fun getInstalledPackageNames(): List<PackageName> = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES)
        .map { it.applicationInfo.packageName }
        .filter { it.isLaunchable() }

    private fun List<PackageName>.toApplications() = mapNotNull {
        try {
            val info = packageManager.getApplicationInfo(it, 0)
            val icon = packageManager.getApplicationIcon(it)
            val name = packageManager.getApplicationLabel(info)
            Application(it, icon, name)
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.w(e, "mapping packageName to application failed")
            null
        }
    }

    private fun PackageName.isLaunchable() = isNotBlank() && packageManager.getLaunchIntentForPackage(this) != null

    private fun Flow<Preferences>.emptyOnIOException() = catch {
        if (it is IOException) {
            Timber.w(it)
            emit(emptyPreferences())
        } else {
            throw it
        }
    }

    // endregion

    // region preference getter/setter

    private fun Preferences.getFirstStart() = get(KEY_FIRST_START) ?: true

    private suspend fun setFirstStart(firstStart: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_FIRST_START] = firstStart
        }
    }

    private fun Preferences.getPackageNames(): List<PackageName> = get(KEY_PACKAGE_NAMES).orEmpty().toList()

    private suspend fun setPackageNames(packageNames: List<PackageName>) {
        dataStore.edit { preferences ->
            preferences[KEY_PACKAGE_NAMES] = packageNames
                .distinct()
                .filter { it.isLaunchable() }
                .take(PACKAGE_NAMES_MAX_COUNT)
                .toSet()
        }
    }

    // endregion

    companion object {

        private val KEY_FIRST_START = preferencesKey<Boolean>("applications_first_start")
        private val KEY_PACKAGE_NAMES = preferencesSetKey<PackageName>("applications_package_names")

        private const val PACKAGE_NAMES_MAX_COUNT = 15

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
