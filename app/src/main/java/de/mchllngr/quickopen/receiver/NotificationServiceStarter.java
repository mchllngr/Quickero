package de.mchllngr.quickopen.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

        saveRestartingTime(context); // FIXME remove

        // Starts the service
        Intent serviceIntent = new Intent(context, NotificationService.class);
        if (VERSION.SDK_INT >= VERSION_CODES.O)
            context.startForegroundService(serviceIntent);
        else
            context.startService(serviceIntent);
    }

    // FIXME delete
    @SuppressLint("ApplySharedPref")
    private void saveRestartingTime(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();

        String restartingTimes = settings.getString("RestartingTimes", "");

        if (!TextUtils.isEmpty(restartingTimes)) {
            restartingTimes += "\n";
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMAN);
        restartingTimes += simpleDateFormat.format(new Date());

        Timber.d("saveRestartingTime: " + restartingTimes);

        editor.putString("RestartingTimes", restartingTimes);
        editor.commit();
    }
}
