package de.mchllngr.quickero

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import de.mchllngr.quickero.base.DebugApp
import de.mchllngr.quickero.repository.application.ApplicationsRepository
import de.mchllngr.quickero.util.splash.SplashScreenHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("unused")
@HiltAndroidApp
class App : DebugApp() {

    @Inject lateinit var applicationsRepository: ApplicationsRepository

    override fun onCreate() {
        super.onCreate()
        initSplashScreen()
        setDefaultNightMode()
        setDummyPackageNamesOnFirstStart()
    }

    private fun initSplashScreen() {
        registerActivityLifecycleCallbacks(SplashScreenHelper())
    }

    private fun setDefaultNightMode() {
        val nightMode = if (VERSION.SDK_INT >= VERSION_CODES.P) AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM else AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    private fun setDummyPackageNamesOnFirstStart() {
        GlobalScope.launch {
            applicationsRepository.setDummyPackageNamesOnFirstStart()
        }
    }
}
