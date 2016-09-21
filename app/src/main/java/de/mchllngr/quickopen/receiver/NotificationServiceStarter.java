package de.mchllngr.quickopen.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.mchllngr.quickopen.service.NotificationService;

/**
 * {@link BroadcastReceiver} for (re)starting the {@link NotificationService}.
 *
 * @author Michael Langer (<a href="https://github.com/mchllngr" target="_blank">GitHub</a>)
 */
public class NotificationServiceStarter extends BroadcastReceiver {

    /**
     * Start the {@link NotificationService} if called.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Starts the service
        context.startService(new Intent(context, NotificationService.class));
    }
}
