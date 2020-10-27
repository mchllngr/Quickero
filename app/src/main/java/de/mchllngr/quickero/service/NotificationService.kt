package de.mchllngr.quickero.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import de.mchllngr.quickero.repository.application.ApplicationsRepository
import de.mchllngr.quickero.repository.notification.NotificationRepository
import de.mchllngr.quickero.util.notification.CustomNotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Foreground-[Service] for handling the application notification. */
@AndroidEntryPoint
class NotificationService : Service() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    @Inject lateinit var applicationsRepository: ApplicationsRepository
    @Inject lateinit var notificationRepository: NotificationRepository
    @Inject lateinit var customNotificationHelper: CustomNotificationHelper

    override fun onCreate() {
        super.onCreate()

        // this is needed because sometimes the OS crashes the app when startForeground is not called in onCreate
        showNotification(customNotificationHelper.createLoadingNotification())
    }

    override fun onDestroy() {
        serviceJob.cancel()
        hideNotification()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(
        intent: Intent,
        flags: Int,
        startId: Int
    ): Int {
        super.onStartCommand(intent, flags, startId)
        init()
        return START_STICKY
    }

    private fun init() {
        serviceScope.launch {
            notificationRepository.enabled
                .combine(applicationsRepository.applications) { enabled, applications -> enabled to applications }
                .collect { (enabled, applications) ->
                    if (enabled && applications.isNotEmpty()) {
                        showNotification(customNotificationHelper.createApplicationNotification(applications))
                    } else {
                        stopSelf()
                    }
                }
        }
    }

    /** Shows a given [Notification] for the Foreground[Service]. */
    private fun showNotification(notification: Notification) {
        startForeground(NOTIFICATION_ID, notification)
    }

    /** Hides the notification while removing [Service] from foreground state. */
    private fun hideNotification() {
        stopForeground(true)
    }

    companion object {

        private const val NOTIFICATION_ID = 12752
    }
}
