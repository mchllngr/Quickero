package de.mchllngr.quickero.util

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.pandulapeter.beagle.Beagle
import com.pandulapeter.beagle.common.configuration.Behavior
import com.pandulapeter.beagle.common.contracts.BeagleListItemContract
import com.pandulapeter.beagle.log.BeagleLogger
import com.pandulapeter.beagle.modules.AnimationDurationSwitchModule
import com.pandulapeter.beagle.modules.AppInfoButtonModule
import com.pandulapeter.beagle.modules.DeveloperOptionsButtonModule
import com.pandulapeter.beagle.modules.DeviceInfoModule
import com.pandulapeter.beagle.modules.DividerModule
import com.pandulapeter.beagle.modules.HeaderModule
import com.pandulapeter.beagle.modules.ItemListModule
import com.pandulapeter.beagle.modules.KeylineOverlaySwitchModule
import com.pandulapeter.beagle.modules.LifecycleLogListModule
import com.pandulapeter.beagle.modules.LogListModule
import com.pandulapeter.beagle.modules.PaddingModule
import com.pandulapeter.beagle.modules.ScreenCaptureToolboxModule
import com.pandulapeter.beagle.modules.SectionHeaderModule
import de.mchllngr.quickero.BuildConfig
import de.mchllngr.quickero.R
import java.lang.ref.WeakReference

class DebugDrawer(private val app: Application) {

    private var currentActivity: WeakReference<AppCompatActivity>? = null
    private val currentActivityCallback = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(
            activity: Activity,
            savedInstanceState: Bundle?
        ) {
            if (activity !is AppCompatActivity) return
            currentActivity = WeakReference(activity)
        }

        override fun onActivityDestroyed(activity: Activity) {
            if (currentActivity?.get() == activity) currentActivity = null
        }

        override fun onActivityStarted(activity: Activity) = Unit

        override fun onActivityResumed(activity: Activity) = Unit

        override fun onActivityPaused(activity: Activity) = Unit

        override fun onActivityStopped(activity: Activity) = Unit

        override fun onActivitySaveInstanceState(
            activity: Activity,
            outState: Bundle
        ) = Unit
    }

    fun init() {
        initBeagle()
    }

    private fun initBeagle() {
        Beagle.initialize(
            application = app,
            behavior = Behavior(
                shakeThreshold = null,
                logger = BeagleLogger
            )
        )
        Beagle.set(
            HeaderModule(
                title = app.getString(R.string.app_name),
                subtitle = BuildConfig.APPLICATION_ID,
                text = "${BuildConfig.BUILD_TYPE} v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
            ),
            AppInfoButtonModule(),
            DeveloperOptionsButtonModule(),
            PaddingModule(),
            SectionHeaderModule("General"),
            ItemListModule(
                title = "App Night Mode",
                items = AppNightModeItem.values().toList(),
                onItemSelected = {
                    currentActivity?.get()?.apply {
                        delegate.localNightMode = it.value
                        recreate()
                    }
                }
            ),
            KeylineOverlaySwitchModule(),
            AnimationDurationSwitchModule(),
            ScreenCaptureToolboxModule(),
            DividerModule(),
            SectionHeaderModule("Logs"),
            LogListModule(),
            LifecycleLogListModule(),
            DividerModule(),
            SectionHeaderModule("Other"),
            DeviceInfoModule()
        )

        app.registerActivityLifecycleCallbacks(currentActivityCallback)
    }

    private enum class AppNightModeItem(
        override val title: CharSequence,
        val value: Int
    ) : BeagleListItemContract {
        MODE_NIGHT_UNSPECIFIED("UNSPECIFIED", AppCompatDelegate.MODE_NIGHT_UNSPECIFIED),
        MODE_NIGHT_FOLLOW_SYSTEM("FOLLOW_SYSTEM", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM),
        MODE_NIGHT_NO("NO", AppCompatDelegate.MODE_NIGHT_NO),
        MODE_NIGHT_YES("YES", AppCompatDelegate.MODE_NIGHT_YES),
        MODE_NIGHT_AUTO_BATTERY("AUTO_BATTERY", AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
    }
}
