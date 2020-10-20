package de.mchllngr.quickero.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import de.mchllngr.quickero.R;
import de.mchllngr.quickero.model.ApplicationModel;
import de.mchllngr.quickero.module.main.MainActivity;
import de.mchllngr.quickero.service.StartApplicationService;
import timber.log.Timber;

import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES;

/**
 * Helper-class for easier handling of the custom notification.
 */
public class CustomNotificationHelper {

    public static final String CHANNEL_DEFAULT_ID = "default";
    private static final String CHANNEL_ERROR_ID = "error";

    private static final int NOTIFICATION_ERROR_ID = 56422;

    private static final int NOTIFICATION_ICON_ID = R.drawable.ic_speaker_notes_white_24dp;
    private static final int NOTIFICATION_ERROR_ICON_ID = R.drawable.ic_error_outline_white_24dp;

    private static final String ERROR_MSG_COULD_NOT_SHOW_NOTIFICATION = "Could not show notification";

    /**
     * Array of every layout used.
     */
    private static final int[] LAYOUT_IDS_CUSTOM_CONTENT = {
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
    };
    /**
     * Array of every iconId used.
     */
    private static final int[] ICON_IDS_CUSTOM_CONTENT = {
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
    };

    /**
     * Used {@link Context}.
     */
    private final Context context;

    /**
     * Constructor for initialising.
     *
     * @param context {@link Context}
     */
    public CustomNotificationHelper(@NonNull Context context) {
        this.context = context;
    }

    /**
     * Checks and prepares the given array of {@link ApplicationModel}s and returns the notification.
     *
     * @param applicationModels array of {@link ApplicationModel}s to show in notification
     */
    @Nullable
    public Notification getCustomNotification(ApplicationModel... applicationModels) {
        int maxAppsInNotification = context.getResources().getInteger(R.integer.max_apps_in_notification);
        if (applicationModels == null || applicationModels.length <= 0 || applicationModels.length > maxAppsInNotification)
            return null;

        applicationModels = removeEmptyItemsFromArray(applicationModels);

        // get custom notification view for length
        RemoteViews customContentView = new RemoteViews(
                context.getPackageName(),
                LAYOUT_IDS_CUSTOM_CONTENT[applicationModels.length - 1]
        );

        long currentTimeMillis = System.currentTimeMillis();

        for (int i = 0; i < applicationModels.length; i++) {
            // set iconBitmap
            customContentView.setImageViewBitmap(
                    ICON_IDS_CUSTOM_CONTENT[i],
                    applicationModels[i].iconBitmap
            );

            // set PendingIntent
            Intent resultIntent = new Intent(context, StartApplicationService.class);
            resultIntent.putExtra(
                    context.getString(R.string.key_package_name),
                    applicationModels[i].packageName
            );
            // needed to make the PendingIntent 'unique' so multiple PendingIntents can be active at the same time
            long uniqueId = Long.MAX_VALUE - currentTimeMillis - (i * 1000);
            resultIntent.setAction(Long.toString(uniqueId));
            PendingIntent pendingIntent = PendingIntent.getService(
                    context,
                    0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            customContentView.setOnClickPendingIntent(ICON_IDS_CUSTOM_CONTENT[i], pendingIntent);
        }

        return getNotificationWithCustomContentView(customContentView);
    }

    /**
     * Removes empty items from given array of {@link ApplicationModel}s.
     *
     * @param applicationModels array of {@link ApplicationModel}s
     * @return array of {@link ApplicationModel}s without empty items
     */
    private ApplicationModel[] removeEmptyItemsFromArray(ApplicationModel[] applicationModels) {
        // remove items with empty packageName or iconBitmap from array
        List<ApplicationModel> tempApplicationModels = new ArrayList<>();
        for (ApplicationModel applicationModel : applicationModels)
            if (!TextUtils.isEmpty(applicationModel.packageName) && applicationModel.iconBitmap != null)
                tempApplicationModels.add(applicationModel);

        return tempApplicationModels.toArray(new ApplicationModel[0]);
    }

    /**
     * Creates the NotificationChannels for Android Oreo.
     */
    public void createNotificationChannels(@NonNull NotificationManager notificationManager) {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            // default
            String nameDefault = context.getString(R.string.notification_channel_default_name);
            NotificationChannel channelDefault = new NotificationChannel(CHANNEL_DEFAULT_ID, nameDefault, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channelDefault);

            // error
            String nameError = context.getString(R.string.notification_channel_error_name);
            NotificationChannel channelError = new NotificationChannel(CHANNEL_ERROR_ID, nameError, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channelError);
        }
    }

    /**
     * Returns a notification with a given custom {@link RemoteViews}.
     *
     * @param customContentView {@link RemoteViews} to show in the notification
     */
    @Nullable
    private Notification getNotificationWithCustomContentView(RemoteViews customContentView) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            Timber.d(ERROR_MSG_COULD_NOT_SHOW_NOTIFICATION);
            return null;
        }

        createNotificationChannels(notificationManager);

        return createNotification(customContentView);
    }

    /**
     * Creates the {@link Notification} for the given {@link RemoteViews}.
     *
     * @param customContentView {@link RemoteViews} for showing inside the {@link Notification}
     * @return {@link Notification}
     */
    @NonNull
    private Notification createNotification(RemoteViews customContentView) {
        return new NotificationCompat.Builder(context, CHANNEL_DEFAULT_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(NOTIFICATION_ICON_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setShowWhen(false)
                .setCustomContentView(customContentView)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
    }

    /**
     * Returns a loading {@link Notification}.
     *
     * @return {@link Notification} showing loading-texts
     */
    @Nullable
    public Notification getLoadingNotification() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            Timber.d(ERROR_MSG_COULD_NOT_SHOW_NOTIFICATION);
            return null;
        }

        createNotificationChannels(notificationManager);

        return createLoadingNotification();
    }

    /**
     * Creates a loading {@link Notification}.
     *
     * @return {@link Notification} showing loading-texts
     */
    @NonNull
    private Notification createLoadingNotification() {
        return new NotificationCompat.Builder(context, CHANNEL_DEFAULT_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(NOTIFICATION_ICON_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setShowWhen(false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentTitle(context.getString(R.string.notification_service_loading_title))
                .setContentText(context.getString(R.string.notification_service_loading_text))
                .build();
    }

    /**
     * Shows an error {@link Notification} when the version is not supported anymore.
     */
    public void showVersionNotSupportedNotification() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            Timber.d(ERROR_MSG_COULD_NOT_SHOW_NOTIFICATION);
            return;
        }

        createNotificationChannels(notificationManager);
        notificationManager.notify(NOTIFICATION_ERROR_ID, createVersionNotSupportedNotification());
    }

    /**
     * Creates a version-not-supported {@link Notification}.
     */
    @NonNull
    private Notification createVersionNotSupportedNotification() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ERROR_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(context, CHANNEL_ERROR_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(NOTIFICATION_ERROR_ICON_ID)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(context.getString(R.string.notification_version_not_supported_title, context.getString(R.string.app_name)))
                .setContentText(context.getString(R.string.notification_version_not_supported_text, context.getString(R.string.app_name)))
                .setStyle(new NotificationCompat.BigTextStyle())
                .setContentIntent(pendingIntent)
                .build();
    }
}
