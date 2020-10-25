package de.mchllngr.quickero.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import de.mchllngr.quickero.repository.application.ApplicationsRepository
import de.mchllngr.quickero.service.NotificationService
import de.mchllngr.quickero.util.notification.NotificationHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * [BroadcastReceiver] for (re)starting the [NotificationService] called when
 * [Intent.ACTION_BOOT_COMPLETED] or [Intent.ACTION_MY_PACKAGE_REPLACED] is received.
 */
@AndroidEntryPoint
class NotificationStarterReceiver : BroadcastReceiver() {

    @Inject lateinit var applicationsRepository: ApplicationsRepository
    @Inject lateinit var notificationHelper: NotificationHelper

    override fun onReceive(
        context: Context?,
        intent: Intent?
    ) {
        if (context == null || intent == null) return

        val action = intent.action
        if (Intent.ACTION_BOOT_COMPLETED == action || Intent.ACTION_MY_PACKAGE_REPLACED == action) {
            Timber.d("Action '$action' received")
            GlobalScope.launch {
                applicationsRepository.removeInvalidApplications()
                notificationHelper.startNotificationService()
            }
        } else {
            Timber.w("Unknown action '$action' received")
        }
    }
}
