package de.mchllngr.quickero.util.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.annotation.ColorInt
import androidx.core.app.NotificationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import de.mchllngr.quickero.R
import de.mchllngr.quickero.extension.toBitmap
import de.mchllngr.quickero.repository.application.Application
import de.mchllngr.quickero.service.StartApplicationService
import de.mchllngr.quickero.util.theme.getThemeColor
import javax.inject.Inject
import javax.inject.Singleton

/** Helper-class for easier handling of the custom notifications. */
@Singleton
class CustomNotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun createApplicationNotification(applications: List<Application>): Notification {
        val currentTimeMillis = System.currentTimeMillis()

        // get notification layout for list length
        val customContentView = RemoteViews(
            context.packageName,
            LAYOUT_IDS_CUSTOM_CONTENT[applications.size - 1]
        )

        applications.forEachIndexed { i, application ->
            // set iconBitmap
            customContentView.setImageViewBitmap(
                ICON_IDS_CUSTOM_CONTENT[i],
                application.icon.toBitmap()
            )

            // set PendingIntent
            val resultIntent = Intent(context, StartApplicationService::class.java)
            resultIntent.putExtra(
                StartApplicationService.KEY_PACKAGE_NAME,
                application.packageName
            )

            // needed to make the PendingIntent 'unique' so multiple PendingIntents can be active at the same time
            val uniqueId = Long.MAX_VALUE - currentTimeMillis - i * 1000
            resultIntent.action = uniqueId.toString()

            val pendingIntent = PendingIntent.getService(
                context,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            customContentView.setOnClickPendingIntent(ICON_IDS_CUSTOM_CONTENT[i], pendingIntent)
        }

        return createCustomContentViewNotification(customContentView)
    }

    private fun createCustomContentViewNotification(customContentView: RemoteViews): Notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_DEFAULT_ID)
        .setColor(getColorPrimary())
        .setSmallIcon(NOTIFICATION_ICON_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setShowWhen(false)
        .setCustomContentView(customContentView)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    fun createLoadingNotification(): Notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_DEFAULT_ID)
        .setColor(getColorPrimary())
        .setSmallIcon(NOTIFICATION_ICON_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setShowWhen(false)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentTitle(context.getString(R.string.notification_service_loading_title))
        .setContentText(context.getString(R.string.notification_service_loading_text))
        .build()

    @ColorInt
    private fun getColorPrimary() = context.getThemeColor(R.attr.colorPrimary)

    companion object {

        private const val NOTIFICATION_ICON_ID = R.drawable.ic_logo_notification

        private val LAYOUT_IDS_CUSTOM_CONTENT = intArrayOf(
            R.layout.custom_notification_01,
            R.layout.custom_notification_02,
            R.layout.custom_notification_03,
            R.layout.custom_notification_04,
            R.layout.custom_notification_05,
            R.layout.custom_notification_06,
            R.layout.custom_notification_07,
            R.layout.custom_notification_08,
            R.layout.custom_notification_09,
            R.layout.custom_notification_10,
            R.layout.custom_notification_11,
            R.layout.custom_notification_12,
            R.layout.custom_notification_13,
            R.layout.custom_notification_14,
            R.layout.custom_notification_15
        )

        private val ICON_IDS_CUSTOM_CONTENT = intArrayOf(
            R.id.app_icon_1,
            R.id.app_icon_2,
            R.id.app_icon_3,
            R.id.app_icon_4,
            R.id.app_icon_5,
            R.id.app_icon_6,
            R.id.app_icon_7,
            R.id.app_icon_8,
            R.id.app_icon_9,
            R.id.app_icon_10,
            R.id.app_icon_11,
            R.id.app_icon_12,
            R.id.app_icon_13,
            R.id.app_icon_14,
            R.id.app_icon_15
        )
    }
}
