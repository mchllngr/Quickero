package de.mchllngr.quickopen.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import de.mchllngr.quickopen.R;

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

        // stop service
        stopSelf();
    }

    /**
     * Starts the application with the given package-name.
     *
     * @param packageName package-name from selected application
     */
    private void startApplication(String packageName) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent != null)
            startActivity(launchIntent);
    }
}
