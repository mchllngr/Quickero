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

        saveRestartingTime(context); // FIXME remove

        // Starts the service
        context.startService(new Intent(context, NotificationService.class));
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
