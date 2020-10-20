package de.mchllngr.quickero.receiver;

import android.content.BroadcastReceiver;
import android.content.Intent;

import androidx.annotation.Nullable;
import de.mchllngr.quickero.service.NotificationService;

/**
 * {@link BroadcastReceiver} for (re)starting the {@link NotificationService} called when {@link Intent#ACTION_MY_PACKAGE_REPLACED} is received.
 */
public class MyPackageReplacedReceiver extends NotificationStarterReceiver {

    @Override
    protected boolean isCorrectAction(@Nullable String action) {
        return Intent.ACTION_MY_PACKAGE_REPLACED.equals(action);
    }
}
