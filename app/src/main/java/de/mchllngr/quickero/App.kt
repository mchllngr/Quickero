package de.mchllngr.quickero

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import de.mchllngr.quickero.base.DebugApp
import de.mchllngr.quickero.util.SplashScreenHelper

/**
 * [App] for the [android.app.Application]
 */
class App : DebugApp() {
    override fun onCreate() {
        super.onCreate()
        initSplashScreen()
        setDefaultNightMode()
    }

    private fun initSplashScreen() {
        registerActivityLifecycleCallbacks(SplashScreenHelper())
    }

    private fun setDefaultNightMode() {
        val nightMode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM else AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}
