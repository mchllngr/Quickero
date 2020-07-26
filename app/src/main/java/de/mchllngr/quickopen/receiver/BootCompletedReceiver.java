package de.mchllngr.quickopen.receiver;

import android.content.BroadcastReceiver;
import android.content.Intent;

import androidx.annotation.Nullable;
import de.mchllngr.quickopen.service.NotificationService;

/**
 * {@link BroadcastReceiver} for (re)starting the {@link NotificationService} called when {@link Intent#ACTION_BOOT_COMPLETED} is received.
 */
public class BootCompletedReceiver extends NotificationStarterReceiver {

    @Override
    protected boolean isCorrectAction(@Nullable String action) {
        return Intent.ACTION_BOOT_COMPLETED.equals(action);
    }
}
