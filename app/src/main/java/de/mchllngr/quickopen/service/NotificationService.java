package de.mchllngr.quickopen.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.google.gson.Gson;

import java.util.List;

import de.mchllngr.quickopen.R;
import de.mchllngr.quickopen.model.ApplicationModel;
import de.mchllngr.quickopen.util.CustomNotificationHelper;
import de.mchllngr.quickopen.util.GsonPreferenceAdapter;

/**
 * Foreground-{@link Service} for handling the notification
 */
public class NotificationService extends Service {

    /**
     * Determines if the notification is enabled and should be shown.
     */
    private boolean notificationEnabled = true;
    /**
     * {@link CustomNotificationHelper}-reference for easier handling of the custom notification.
     */
    private CustomNotificationHelper customNotificationHelper;
    /**
     * {@link Preference}-reference for easier usage of the saved value for notificationEnabled
     * in the {@link RxSharedPreferences}.
     */
    private Preference<Boolean> notificationEnabledPref;
    /**
     * {@link Preference}-reference for easier usage of the saved value for packageNames in the
     * {@link RxSharedPreferences}.
     */
    private Preference<List> packageNamesPref;

    @Override
    public void onCreate() {
        super.onCreate();

        RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(PreferenceManager.getDefaultSharedPreferences(this));

        customNotificationHelper = new CustomNotificationHelper(this);

        notificationEnabledPref = rxSharedPreferences.getBoolean(
                getString(R.string.pref_notification_enabled),
                Boolean.parseBoolean(getString(R.string.pref_notification_enabled_default_value))
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
                packageNamesPref == null)
            onError();

        // subscribe to changes in notificationEnabledPref
        notificationEnabledPref.asObservable().subscribe(enabled -> {
            this.notificationEnabled = enabled;

            if (enabled) {
                ApplicationModel[] applicationModels = ApplicationModel.prepareApplicationModelsArray(this, packageNamesPref.get());
                showNotification(applicationModels);
            } else
                hideNotification();
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
     * Calls {@link CustomNotificationHelper} to show the notification with given array of {@link ApplicationModel}s.
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
