package de.mchllngr.quickero

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import de.mchllngr.quickero.base.DebugApp
import de.mchllngr.quickero.util.splash.SplashScreenHelper

@Suppress("unused")
@HiltAndroidApp
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
