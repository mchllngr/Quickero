package de.mchllngr.quickopen.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

import de.mchllngr.quickopen.R;
import de.mchllngr.quickopen.model.ApplicationModel;
import de.mchllngr.quickopen.service.StartApplicationService;
import timber.log.Timber;

/**
 * Helper-class for easier handling of the custom notification.
 *
 * @author Michael Langer (<a href="https://github.com/mchllngr" target="_blank">GitHub</a>)
 */
// TODO inject with dagger ?
public class CustomNotificationHelper {

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
     * Id used for showing the notification.
     */
    private int notificationId;
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
     * Reference of the last array of applications shown in the notification.
     * <p>
     * Used for restarting the notification.
     *
     * @see CustomNotificationHelper#reloadNotification()
     */
    private ApplicationModel[] lastApplicationModels;

    /**
     * Constructor for initialising.
     *
     * @param context            {@link Context}
     * @param notificationId     id used for showing the notification
     * @param notificationIconId iconId used for showing the notification
     */
    public CustomNotificationHelper(@NonNull Context context,
                                    int notificationId,
                                    int notificationIconId) {
        this.context = context;
        this.notificationId = notificationId;
        this.notificationIconId = notificationIconId;
    }

    /**
     * Setter for {@code notificationVisibility}.
     *
     * @param notificationVisibility {@code notificationVisibility}
     */
    public void setNotificationVisibility(int notificationVisibility, boolean reloadNotification) {
        this.notificationVisibility = notificationVisibility;

        if (reloadNotification)
            reloadNotification();
    }

    /**
     * Setter for {@code notificationPriority}.
     *
     * @param notificationPriority {@code notificationPriority}
     */
    public void setNotificationPriority(int notificationPriority, boolean reloadNotification) {
        this.notificationPriority = notificationPriority;

        if (reloadNotification)
            reloadNotification();
    }

    /**
     * Checks and prepares the given array of {@link ApplicationModel}s and shows them
     * in a notification.
     *
     * @param applicationModels array of {@link ApplicationModel}s to show in notification
     */
    // TODO make return boolean and return false if notification is not shown for better error handling
    public void showCustomNotification(ApplicationModel... applicationModels) {
        int maxAppsInNotification = context.getResources()
            .getInteger(R.integer.max_apps_in_notification);
        if (applicationModels == null ||
                applicationModels.length <= 0 ||
                applicationModels.length > maxAppsInNotification)
            return;

        applicationModels = removeEmptyItemsFromArray(applicationModels);

        // save applicationModels temporarily
        lastApplicationModels = applicationModels;

        // get custom notification view for length
        RemoteViews customContentView = new RemoteViews(
                context.getPackageName(),
                LAYOUT_IDS_CUSTOM_CONTENT[applicationModels.length - 1]
        );

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
            // needed to make the PendingIntent 'unique' so multiple PendingIntents can
            // be active at the same time
            resultIntent.setAction(Long.toString(System.currentTimeMillis()));
            PendingIntent pendingIntent = PendingIntent.getService(
                    context,
                    0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            customContentView.setOnClickPendingIntent(ICON_IDS_CUSTOM_CONTENT[i], pendingIntent);
        }

        showNotificationWithCustomContentView(customContentView);
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
            if (!TextUtils.isEmpty(applicationModel.packageName) &&
                    applicationModel.iconBitmap != null)
                tempApplicationModels.add(applicationModel);

        return tempApplicationModels.toArray(new ApplicationModel[tempApplicationModels.size()]);
    }

    /**
     * Shows notification with a given custom {@link RemoteViews}.
     *
     * @param customContentView {@link RemoteViews} to show in the notification
     */
    private void showNotificationWithCustomContentView(RemoteViews customContentView) {
        hideNotification();

        // create notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder
                .setSmallIcon(notificationIconId)
                .setAutoCancel(false)
                .setOngoing(true)
                .setShowWhen(false)
                .setCustomContentView(customContentView)
                .setVisibility(notificationVisibility)
                .setPriority(notificationPriority)
                .build();

        // show notification
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notification);
    }

    /**
     * Hide the notification.
     */
    public void hideNotification() {
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }

    /**
     * Reloads the notification with {@code lastApplicationModels}.
     */
    private void reloadNotification() {
        if (lastApplicationModels != null)
            showCustomNotification(lastApplicationModels);
    }
}
