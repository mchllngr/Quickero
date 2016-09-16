package de.mchllngr.quickopen.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.mchllngr.quickopen.service.NotificationService;

/**
 * {@link BroadcastReceiver} for restarting the {@link NotificationService} when it sends the
 * that its about to be destroyed.
 *
 * @author Michael Langer (<a href="https://github.com/mchllngr" target="_blank">GitHub</a>)
 */
public class NotificationServiceRestarter extends BroadcastReceiver {

    /**
     * Restarts the {@link NotificationService} if called.
     *
     * @param context {@link Context}
     * @param intent  {@link Intent}
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // restarts the service
        context.startService(new Intent(context, NotificationService.class));
    }
}
