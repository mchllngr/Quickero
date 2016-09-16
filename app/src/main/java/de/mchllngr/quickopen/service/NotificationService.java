package de.mchllngr.quickopen.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import de.mchllngr.quickopen.R;
import de.mchllngr.quickopen.model.ApplicationModel;
import de.mchllngr.quickopen.util.CustomNotificationHelper;
import de.mchllngr.quickopen.util.GsonPreferenceAdapter;
import rx.functions.Action1;

/**
 * Service for handling the notification
 *
 * @author Michael Langer (<a href="https://github.com/mchllngr" target="_blank">GitHub</a>)
 */
public class NotificationService extends Service {

    /**
     * Name of an intentFilter used to notify the
     * {@link de.mchllngr.quickopen.receiver.NotificationServiceRestarter}.
     *
     * @see de.mchllngr.quickopen.receiver.NotificationServiceRestarter
     */
    private static final String INTENT_FILTER_NAME = "de.mchllngr.quickopen.service.RestartService";

    /**
     * Determines if the current instance of the service should try to send a message to
     * {@link de.mchllngr.quickopen.receiver.NotificationServiceRestarter} if its about to be
     * destroyed for restarting the service.
     */
    private boolean shouldRestartAfterDestroy = true;
    /**
     * {@link CustomNotificationHelper}-reference for easier handling of the custom notification.
     */
    private CustomNotificationHelper customNotificationHelper;
    /**
     * {@link Preference}-reference for easier usage of the saved value for packageVisibility in the
     * {@link RxSharedPreferences}.
     */
    private Preference<Integer> packageVisibilityPref;
    /**
     * {@link Preference}-reference for easier usage of the saved value for packagePriority in the
     * {@link RxSharedPreferences}.
     */
    private Preference<Integer> packagePriorityPref;
    /**
     * {@link Preference}-reference for easier usage of the saved value for packageNames in the
     * {@link RxSharedPreferences}.
     */
    private Preference<List> packageNamesPref;

    @Override
    public void onCreate() {
        super.onCreate();

        if (customNotificationHelper == null)
            customNotificationHelper = new CustomNotificationHelper(
                    this, getResources().getInteger(R.integer.notification_id),
                    R.mipmap.ic_launcher);

        RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(
                PreferenceManager.getDefaultSharedPreferences(this));

        if (packageVisibilityPref == null)
            packageVisibilityPref = rxSharedPreferences.getInteger(
                    getString(R.string.pref_notification_visibility),
                    NotificationCompat.VISIBILITY_PUBLIC);

        if (packagePriorityPref == null)
            packagePriorityPref = rxSharedPreferences.getInteger(
                    getString(R.string.pref_notification_priority),
                    Notification.PRIORITY_DEFAULT);

        if (packageNamesPref == null) {
            GsonPreferenceAdapter<List> adapter =
                    new GsonPreferenceAdapter<>(new Gson(), List.class);
            packageNamesPref = rxSharedPreferences.getObject(
                    getString(R.string.pref_package_names), null, adapter);
        }
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
        if (packageNamesPref == null ||
                packageVisibilityPref == null ||
                packagePriorityPref == null ||
                customNotificationHelper == null)
            onError();

        // subscribe to changes in packageVisibilityPref
        packageVisibilityPref.asObservable().subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer packageVisibility) {
                if (packageVisibility != null)
                    customNotificationHelper.setNotificationVisibility(packageVisibility);
            }
        });

        // subscribe to changes in packagePriorityPref
        packagePriorityPref.asObservable().subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer packagePriority) {
                if (packagePriority != null)
                    customNotificationHelper.setNotificationPriority(packagePriority);
            }
        });

        // subscribe to changes in packageNamesPref
        packageNamesPref.asObservable().subscribe(new Action1<List>() {
            @Override
            public void call(List list) {
                if (list == null || list.isEmpty()) return;

                List<ApplicationModel> applicationModels = new ArrayList<>();

                for (Object o : list)
                    if (o instanceof String) {
                        ApplicationModel applicationModel = ApplicationModel.
                                getApplicationModelForPackageName(
                                        NotificationService.this, (String) o);

                        if (applicationModel != null)
                            applicationModels.add(applicationModel);
                    }

                showNotification(applicationModels.toArray(
                        new ApplicationModel[applicationModels.size()]));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        hideNotification();

        if (shouldRestartAfterDestroy) {
            // sends broadcast when service is about to be destroyed
            Intent broadcastIntent = new Intent(INTENT_FILTER_NAME);
            sendBroadcast(broadcastIntent);
        }
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
        if (customNotificationHelper != null)
            customNotificationHelper.showCustomNotification(applicationModels);
    }

    /**
     * Calls {@link CustomNotificationHelper} to hide the notification.
     */
    private void hideNotification() {
        if (customNotificationHelper != null)
            customNotificationHelper.hideNotification();
    }

    /**
     * Shows an error-message, stops the {@link Service} from restarting by setting
     * {@code shouldRestartAfterDestroy} and stops the {@link Service}.
     */
    private void onError() {
        showErrorMessage();
        shouldRestartAfterDestroy = false;
        stopSelf();
    }

    /**
     * Shows the error-message.
     */
    private void showErrorMessage() {
        // TODO replace Toast with Error-Notification (click starts activity)
        Toast.makeText(this, getString(R.string.notification_service_error),
                Toast.LENGTH_SHORT).show();
    }
}