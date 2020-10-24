package de.mchllngr.quickero.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.mchllngr.quickero.model.ApplicationModel
import timber.log.Timber

/** [BroadcastReceiver] for removing apps from the list when uninstalled.*/
class PackageFullyRemovedReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context?,
        intent: Intent?
    ) {
        if (context == null || intent == null) return

        val action = intent.action
        if (Intent.ACTION_PACKAGE_FULLY_REMOVED == action) {
            Timber.d("Action '$action' received")
            ApplicationModel.removeNotLaunchableAppsFromList(context)
        } else {
            Timber.w("Unknown action '$action' received")
        }
    }
}
