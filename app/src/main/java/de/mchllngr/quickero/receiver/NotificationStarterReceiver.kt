package de.mchllngr.quickero.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import de.mchllngr.quickero.model.ApplicationModel
import de.mchllngr.quickero.service.NotificationService
import timber.log.Timber

/**
 * [BroadcastReceiver] for (re)starting the [NotificationService] called when
 * [Intent.ACTION_BOOT_COMPLETED] or [Intent.ACTION_MY_PACKAGE_REPLACED] is received.
 */
class NotificationStarterReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context?,
        intent: Intent?
    ) {
        if (context == null || intent == null) return

        val action = intent.action
        if (Intent.ACTION_BOOT_COMPLETED == action || Intent.ACTION_MY_PACKAGE_REPLACED == action) {
            Timber.d("Action '$action' received")
            ApplicationModel.removeNotLaunchableAppsFromList(context)
            startNotificationService(context)
        } else {
            Timber.w("Unknown action '$action' received")
        }
    }

    private fun startNotificationService(context: Context) {
        val serviceIntent = Intent(context, NotificationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}
