package de.mchllngr.quickopen.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.mchllngr.quickopen.service.NotificationService;
import timber.log.Timber;

import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES;

/**
 * {@link BroadcastReceiver} for (re)starting the {@link NotificationService}.
 */
public class NotificationServiceStarter extends BroadcastReceiver {

    /**
     * Start the {@link NotificationService} if called with the correct actions.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!Intent.ACTION_BOOT_COMPLETED.equals(action) && !Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
            Timber.w("Unknown action '" + action + "'");
            return;
        }

        // Starts the service
        Intent serviceIntent = new Intent(context, NotificationService.class);
        if (VERSION.SDK_INT >= VERSION_CODES.O)
            context.startForegroundService(serviceIntent);
        else
            context.startService(serviceIntent);
    }
}
