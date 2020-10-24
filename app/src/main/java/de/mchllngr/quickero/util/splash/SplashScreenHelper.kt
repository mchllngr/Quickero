package de.mchllngr.quickero.util.splash

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.pm.PackageManager
import android.os.Bundle
import de.mchllngr.quickero.R
import timber.log.Timber

/* http://blog.davidmedenjak.com/android/2017/09/02/splash-screens.html */
class SplashScreenHelper : ActivityLifecycleCallbacks {

    override fun onActivityCreated(
        activity: Activity,
        savedInstanceState: Bundle?
    ) {
        // apply the actual theme given by meta-data
        try {
            val activityInfo = activity.packageManager.getActivityInfo(activity.componentName, PackageManager.GET_META_DATA)
            val theme = when {
                activityInfo.metaData != null -> activityInfo.metaData.getInt(KEY_THEME, R.style.AppTheme)
                else -> R.style.AppTheme
            }
            activity.setTheme(theme)
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e(e)
        }
    }

    override fun onActivityStarted(activity: Activity) = Unit

    override fun onActivityResumed(activity: Activity) = Unit

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(
        activity: Activity,
        bundle: Bundle
    ) = Unit

    override fun onActivityDestroyed(activity: Activity) = Unit

    companion object {

        private const val KEY_THEME = "theme"
    }
}
