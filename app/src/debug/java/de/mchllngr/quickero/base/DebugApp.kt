package de.mchllngr.quickero.base

import android.app.Application
import com.pandulapeter.beagle.log.BeagleLogger
import de.mchllngr.quickero.util.DebugDrawer
import timber.log.Timber
import timber.log.Timber.DebugTree

/**
 * Base-class used for debug initializations.
 */
open class DebugApp : Application() {

    @Suppress("LeakingThis") private val debugDrawer = DebugDrawer(this)

    override fun onCreate() {
        super.onCreate()
        initTimber()
        debugDrawer.init()
    }

    /**
     * Initialises [Timber] with debug configuration
     */
    private fun initTimber() {
        Timber.plant(object : DebugTree() {

            // Add the line number to the tag
            override fun createStackElementTag(element: StackTraceElement): String? {
                return super.createStackElementTag(element) + '#' + element.lineNumber
            }

            override fun log(
                priority: Int,
                tag: String?,
                message: String,
                t: Throwable?
            ) {
                BeagleLogger.log("[$tag] $message", "Timber", t?.stackTraceToString())
                super.log(priority, tag, message, t)
            }
        })
    }
}
