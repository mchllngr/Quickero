package de.mchllngr.quickero.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;

import androidx.preference.PreferenceManager;
import de.mchllngr.quickero.R;
import de.mchllngr.quickero.util.CustomNotificationHelper;
import de.mchllngr.quickero.util.FirebaseUtils;

/**
 * {@link IntentService} for starting the clicked application from the notification.
 */
public class StartApplicationService extends IntentService {

    public StartApplicationService() {
        super(StartApplicationService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // get packageName and start selected application
        Bundle extras = intent.getExtras();
        if (extras != null && extras.containsKey(getString(R.string.key_package_name))) {
            String packageName = extras.getString(getString(R.string.key_package_name));
            startApplication(packageName);
        }

        // close navigation bar
        Intent i = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        sendBroadcast(i);

        // check if version is still supported
        FirebaseUtils.isVersionSupported(supported -> {
            handleVersionSupportedResult(supported);

            // stop service
            stopSelf();
        });
    }

    /**
     * Starts the application with the given package-name.
     *
     * @param packageName package-name from selected application
     */
    private void startApplication(String packageName) {
        FirebaseUtils.logStartApplicationEvent(this);
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent != null)
            startActivity(launchIntent);
    }

    private void handleVersionSupportedResult(boolean supported) {
        if (!supported) {
            // set notification disabled
            RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(PreferenceManager.getDefaultSharedPreferences(this));
            Preference<Boolean> notificationEnabledPref = rxSharedPreferences.getBoolean(
                    getString(R.string.pref_notification_enabled),
                    Boolean.parseBoolean(getString(R.string.pref_notification_enabled_default_value))
            );
            notificationEnabledPref.set(false);

            // show notification
            new CustomNotificationHelper(this).showVersionNotSupportedNotification();
        }
    }
}
