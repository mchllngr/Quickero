package de.mchllngr.quickopen.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.mchllngr.quickopen.model.ApplicationModel;
import de.mchllngr.quickopen.service.NotificationService;
import timber.log.Timber;

/**
 * {@link BroadcastReceiver} for (re)starting the {@link NotificationService}.
 *
 * This abstract approach is needed, because each action needs it's own {@link BroadcastReceiver}.
 */
abstract class NotificationStarterReceiver extends BroadcastReceiver {

    /**
     * Returns true when the received action is correct for this {@link BroadcastReceiver}.
     *
     * @param action received action
     * @return true is the action is correct for this {@link BroadcastReceiver}
     */
    protected abstract boolean isCorrectAction(@Nullable String action);

    /**
     * Start the {@link NotificationService} if called with the correct actions.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null) return;

        String action = intent.getAction();
        if (isCorrectAction(action)) {
            Timber.d("Action '" + action + "' received");
            ApplicationModel.removeNotLaunchableAppsFromList(context);
            startNotificationService(context);
        } else
            Timber.w("Unknown action '" + action + "' received");
    }

    protected void startNotificationService(@NonNull Context context) {
        Intent serviceIntent = new Intent(context, NotificationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(serviceIntent);
        else
            context.startService(serviceIntent);
    }
}
