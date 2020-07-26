package de.mchllngr.quickopen.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import de.mchllngr.quickopen.model.ApplicationModel;
import de.mchllngr.quickopen.service.NotificationService;
import timber.log.Timber;

/**
 * {@link BroadcastReceiver} for removing apps from the list when uninstalled.
 */
public class PackageFullyRemovedReceiver extends BroadcastReceiver {

    /**
     * Checks if  the {@link NotificationService} if called with the correct actions.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null) return;

        String action = intent.getAction();
        if (isCorrectAction(action)) {
            Timber.d("Action '" + action + "' received");
            ApplicationModel.removeNotLaunchableAppsFromList(context);
        } else
            Timber.w("Unknown action '" + action + "' received");
    }

    /**
     * Returns true when the received action is correct for this {@link BroadcastReceiver}.
     *
     * @param action received action
     * @return true is the action is correct for this {@link BroadcastReceiver}
     */
    private boolean isCorrectAction(@Nullable String action) {
        return Intent.ACTION_PACKAGE_FULLY_REMOVED.equals(action);
    }
}
