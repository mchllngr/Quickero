package de.mchllngr.quickero.util.notification

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import de.mchllngr.quickero.R
import de.mchllngr.quickero.repository.application.Application
import de.mchllngr.quickero.service.NotificationService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: NotificationManager,
    private val customNotificationHelper: CustomNotificationHelper
) {

    fun startNotificationService() {
        val serviceIntent = Intent(context, NotificationService::class.java)
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    fun createApplicationNotification(applications: List<Application>): Notification {
        createNotificationChannels()
        return customNotificationHelper.createApplicationNotification(applications)
    }

    fun createLoadingNotification(): Notification {
        createNotificationChannels()
        return customNotificationHelper.createLoadingNotification()
    }

    fun isNotificationEnabled() = isNotificationEnabled(CHANNEL_DEFAULT_ID)

    @Suppress("SameParameterValue")
    private fun isNotificationEnabled(channelId: String?): Boolean {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            if (channelId == null) return false

            if (notificationManager.getNotificationChannel(channelId) == null) createNotificationChannels()

            val channel: NotificationChannel = notificationManager.getNotificationChannel(channelId)
            return channel.importance != NotificationManager.IMPORTANCE_NONE
        } else {
            return NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    private fun createNotificationChannels() {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(CHANNEL_DEFAULT_ID, context.getString(R.string.notification_channel_default_name), NotificationManager.IMPORTANCE_LOW).apply {
                    setShowBadge(false)
                    enableLights(false)
                    enableVibration(false)
                },
                NotificationChannel(CHANNEL_ERROR_ID, context.getString(R.string.notification_channel_error_name), NotificationManager.IMPORTANCE_HIGH)
            )
            notificationManager.createNotificationChannels(channels)
        }
    }

    fun openSettings(activity: Activity) {
        openSettings(activity, CHANNEL_DEFAULT_ID)
    }

    @Suppress("SameParameterValue")
    private fun openSettings(
        activity: Activity,
        channelId: String?
    ) {
        val intent = Intent()
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            if (channelId != null) {
                intent.action = Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
            } else {
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            }
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        } else {
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package", context.packageName)
            intent.putExtra("app_uid", context.applicationInfo.uid)
        }
        activity.startActivity(intent)
    }

    companion object {

        const val CHANNEL_DEFAULT_ID = "default"
        private const val CHANNEL_ERROR_ID = "error"
    }
}
