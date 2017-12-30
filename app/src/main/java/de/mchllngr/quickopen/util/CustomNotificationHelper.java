package de.mchllngr.quickopen.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

import de.mchllngr.quickopen.R;
import de.mchllngr.quickopen.model.ApplicationModel;
import de.mchllngr.quickopen.service.StartApplicationService;
import timber.log.Timber;

import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES;

/**
 * Helper-class for easier handling of the custom notification.
 */
// TODO inject with dagger ?
public class CustomNotificationHelper {

    private static final String CHANNEL_ID = "default";

    /**
     * Array of every layout used.
     */
    private final int[] LAYOUT_IDS_CUSTOM_CONTENT = {
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
    private final int[] ICON_IDS_CUSTOM_CONTENT = {
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
    private Context context;
    /**
     * IconId used for showing the notification.
     */
    private int notificationIconId;
    /**
     * Visibility setting for the notification.
     */
    private int notificationVisibility = NotificationCompat.VISIBILITY_PUBLIC;
    /**
     * Priority setting for the notification.
     */
    private int notificationPriority = Notification.PRIORITY_DEFAULT;

    /**
     * Constructor for initialising.
     *
     * @param context            {@link Context}
     * @param notificationIconId iconId used for showing the notification
     */
    public CustomNotificationHelper(@NonNull Context context, int notificationIconId) {
        this.context = context;
        this.notificationIconId = notificationIconId;
    }

    /**
     * Setter for {@code notificationIconId}.
     *
     * @param notificationIconId {@code notificationIconId}
     */
    public void setNotificationIcon(int notificationIconId) {
        this.notificationIconId = notificationIconId;
    }

    /**
     * Setter for {@code notificationVisibility}.
     *
     * @param notificationVisibility {@code notificationVisibility}
     */
    public void setNotificationVisibility(int notificationVisibility) {
        this.notificationVisibility = notificationVisibility;
    }

    /**
     * Setter for {@code notificationPriority}.
     *
     * @param notificationPriority {@code notificationPriority}
     */
    public void setNotificationPriority(int notificationPriority) {
        this.notificationPriority = notificationPriority;
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

        return tempApplicationModels.toArray(new ApplicationModel[tempApplicationModels.size()]);
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
            Timber.d("Could not show notification");
            return null;
        }

        // create notification channel
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            String name = context.getString(R.string.notification_channel_default_name);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // create notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        return builder
                .setSmallIcon(notificationIconId)
                .setAutoCancel(false)
                .setOngoing(true)
                .setShowWhen(false)
                .setCustomContentView(customContentView)
                .setVisibility(notificationVisibility)
                .setPriority(notificationPriority)
                .build();
    }
}
