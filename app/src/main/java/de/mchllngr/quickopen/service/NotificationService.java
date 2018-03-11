package de.mchllngr.quickopen.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.google.gson.Gson;

import java.util.List;

import de.mchllngr.quickopen.R;
import de.mchllngr.quickopen.model.ApplicationModel;
import de.mchllngr.quickopen.util.CustomNotificationHelper;
import de.mchllngr.quickopen.util.GsonPreferenceAdapter;

import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES;

/**
 * Foreground-{@link Service} for handling the notification
 */
public class NotificationService extends Service {

    private static final int NOTIFICATION_ICON_ID_NOT_VECTOR = R.drawable.ic_notification;
    private static final int NOTIFICATION_ICON_ID_VECTOR = R.drawable.ic_speaker_notes_white_24px;
    private static final int NOTIFICATION_ICON_ID_NOT_VECTOR_TRANSPARENT = R.drawable.ic_notification_blank;
    private static final int NOTIFICATION_ICON_ID_VECTOR_TRANSPARENT = R.drawable.ic_blank_24px;

    /**
     * Allows usage of VectorDrawables when current {@link VERSION} is Android Lollipop or newer.
     */
    private static final boolean USE_VECTOR_DRAWABLES = VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP;

    /**
     * Determines if the notification is enabled and should be shown.
     */
    private boolean notificationEnabled = true;
    /**
     * {@link CustomNotificationHelper}-reference for easier handling of the custom notification.
     */
    private CustomNotificationHelper customNotificationHelper;
    /**
     * {@link Preference}-reference for easier usage of the saved value for transparentIcon
     * in the {@link RxSharedPreferences}.
     */
    private Preference<Boolean> transparentIconPref;
    /**
     * {@link Preference}-reference for easier usage of the saved value for notificationEnabled
     * in the {@link RxSharedPreferences}.
     */
    private Preference<Boolean> notificationEnabledPref;
    /**
     * {@link Preference}-reference for easier usage of the saved value for
     * notificationShowOnLockScreen in the {@link RxSharedPreferences}.
     */
    private Preference<Boolean> notificationShowOnLockScreenPref;
    /**
     * {@link Preference}-reference for easier usage of the saved value for notificationPriority
     * in the {@link RxSharedPreferences}.
     */
    private Preference<String> notificationPriorityPref;
    /**
     * {@link Preference}-reference for easier usage of the saved value for packageNames in the
     * {@link RxSharedPreferences}.
     */
    private Preference<List> packageNamesPref;

    @Override
    public void onCreate() {
        super.onCreate();

        RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(PreferenceManager.getDefaultSharedPreferences(this));

        int notificationIcon = USE_VECTOR_DRAWABLES ? NOTIFICATION_ICON_ID_VECTOR : NOTIFICATION_ICON_ID_NOT_VECTOR;

        customNotificationHelper = new CustomNotificationHelper(this, notificationIcon);

        notificationEnabledPref = rxSharedPreferences.getBoolean(
                getString(R.string.pref_notification_enabled),
                Boolean.parseBoolean(getString(R.string.pref_notification_enabled_default_value))
        );

        transparentIconPref = rxSharedPreferences.getBoolean(
                getString(R.string.pref_transparent_icon),
                Boolean.parseBoolean(getString(R.string.pref_transparent_icon_default_value))
        );

        notificationShowOnLockScreenPref = rxSharedPreferences.getBoolean(
                getString(R.string.pref_notification_show_on_lock_screen),
                Boolean.parseBoolean(getString(R.string.pref_notification_show_on_lock_screen_default_value))
        );

        notificationPriorityPref = rxSharedPreferences.getString(
                getString(R.string.pref_notification_priority),
                getString(R.string.pref_notification_priority_option_default)
        );

        GsonPreferenceAdapter<List> adapter = new GsonPreferenceAdapter<>(new Gson(), List.class);
        packageNamesPref = rxSharedPreferences.getObject(
                getString(R.string.pref_package_names),
                null,
                adapter
        );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        initService();

        return START_STICKY;
    }

    /**
     * Initialises the {@link Service} and used variables.
     */
    private void initService() {
        if (customNotificationHelper == null ||
                notificationShowOnLockScreenPref == null ||
                notificationPriorityPref == null ||
                packageNamesPref == null)
            onError();

        // subscribe to changes in notificationEnabledPref
        notificationEnabledPref.asObservable().subscribe(notificationEnabled -> {
            this.notificationEnabled = notificationEnabled;

            if (notificationEnabled) {
                ApplicationModel[] applicationModels = ApplicationModel.prepareApplicationModelsArray(this, packageNamesPref.get());
                showNotification(applicationModels);
            } else
                hideNotification();
        });

        // subscribe to changes in transparentIconPref
        transparentIconPref.asObservable().subscribe(transparentIcon -> {
            int notificationIcon;
            if (transparentIcon)
                notificationIcon = USE_VECTOR_DRAWABLES ? NOTIFICATION_ICON_ID_VECTOR_TRANSPARENT : NOTIFICATION_ICON_ID_NOT_VECTOR_TRANSPARENT;
            else
                notificationIcon = USE_VECTOR_DRAWABLES ? NOTIFICATION_ICON_ID_VECTOR : NOTIFICATION_ICON_ID_NOT_VECTOR;

            customNotificationHelper.setNotificationIcon(notificationIcon);
        });

        // subscribe to changes in notificationShowOnLockScreenPref
        notificationShowOnLockScreenPref.asObservable().subscribe(notificationShowOnLockScreen -> {
            int notificationVisibility = NotificationCompat.VISIBILITY_PUBLIC;
            if (!notificationShowOnLockScreen)
                notificationVisibility = NotificationCompat.VISIBILITY_SECRET;

            customNotificationHelper.setNotificationVisibility(notificationVisibility);
        });

        // subscribe to changes in notificationPriorityPref
        notificationPriorityPref.asObservable().subscribe(notificationPriority -> {
            if (TextUtils.isEmpty(notificationPriority)) return;

            int notificationPriorityValue = Notification.PRIORITY_MAX;
            if (notificationPriority.equals(getString(R.string.pref_notification_priority_value_high)))
                notificationPriorityValue = Notification.PRIORITY_HIGH;
            else if (notificationPriority.equals(getString(R.string.pref_notification_priority_value_default)))
                notificationPriorityValue = Notification.PRIORITY_DEFAULT;
            else if (notificationPriority.equals(getString(R.string.pref_notification_priority_value_low)))
                notificationPriorityValue = Notification.PRIORITY_LOW;
            else if (notificationPriority.equals(getString(R.string.pref_notification_priority_value_min)))
                notificationPriorityValue = Notification.PRIORITY_MIN;

            customNotificationHelper.setNotificationPriority(notificationPriorityValue);
        });

        // subscribe to changes in packageNamesPref
        packageNamesPref.asObservable().subscribe(list -> {
            ApplicationModel.removeNotLaunchableAppsFromList(this);
            ApplicationModel[] applicationModels = ApplicationModel.prepareApplicationModelsArray(this, list);

            if (applicationModels.length > 0)
                showNotification(applicationModels);
            else
                hideNotification();
        });
    }

    @Override
    public void onDestroy() {
        hideNotification();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Calls {@link CustomNotificationHelper} to show the notification with given array of
     * {@link ApplicationModel}s.
     *
     * @param applicationModels array of {@link ApplicationModel}s to show in notification
     */
    private void showNotification(ApplicationModel... applicationModels) {
        if (notificationEnabled && customNotificationHelper != null) {
            Notification notification = customNotificationHelper.getCustomNotification(applicationModels);
            if (notification != null)
                startForeground(getResources().getInteger(R.integer.notification_id), notification);
            else
                onError();
        } else
            onError();
    }

    /**
     * Hides the notification while removing {@link Service} from foreground state.
     */
    private void hideNotification() {
        stopForeground(true);
    }

    /**
     * Shows an error-message and stops the {@link Service}.
     */
    private void onError() {
        showErrorMessage();
        stopSelf();
    }

    /**
     * Shows the error-message.
     */
    private void showErrorMessage() {
        // TODO replace Toast with Error-Notification (click starts activity)
        Toast.makeText(this, getString(R.string.notification_service_error), Toast.LENGTH_SHORT).show();
    }
}
