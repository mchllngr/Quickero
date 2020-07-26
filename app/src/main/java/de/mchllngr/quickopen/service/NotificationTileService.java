package de.mchllngr.quickopen.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;

import androidx.preference.PreferenceManager;
import de.mchllngr.quickopen.R;
import rx.Subscription;

/**
 * {@link Service} for handling the {@link Tile} for enabling and disabling the {@link android.app.Notification} in Android Nougat and above.
 */
@TargetApi(Build.VERSION_CODES.N)
public class NotificationTileService extends TileService {

    /**
     * {@link Preference}-reference for easier usage of the saved value for notificationEnabled in the {@link RxSharedPreferences}.
     */
    private Preference<Boolean> notificationEnabledPref;
    /**
     * {@link Subscription} from subscribing to {@code notificationEnabledPref}.
     */
    private Subscription notificationEnabledSubscription;

    @Override
    public void onCreate() {
        super.onCreate();

        RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(PreferenceManager.getDefaultSharedPreferences(this));

        notificationEnabledPref = rxSharedPreferences.getBoolean(
                getString(R.string.pref_notification_enabled),
                Boolean.parseBoolean(getString(R.string.pref_notification_enabled_default_value))
        );
    }

    @Override
    public void onStartListening() {
        super.onStartListening();

        // subscribe to changes in notificationEnabledPref
        notificationEnabledSubscription = notificationEnabledPref
                .asObservable()
                .subscribe(notificationEnabled -> {
                    if (notificationEnabled)
                        updateTileState(Tile.STATE_ACTIVE);
                    else
                        updateTileState(Tile.STATE_INACTIVE);
                });
    }

    /**
     * Updates the state of the shown {@link Tile}.
     */
    private void updateTileState(int state) {
        Tile tile = getQsTile();
        if (tile != null) {
            tile.setState(state);
            Icon icon = tile.getIcon();
            switch (state) {
                case Tile.STATE_ACTIVE:
                    icon.setTint(Color.WHITE);
                    break;
                case Tile.STATE_INACTIVE:
                case Tile.STATE_UNAVAILABLE:
                default:
                    icon.setTint(Color.GRAY);
                    break;
            }
            tile.updateTile();
        }
    }

    @Override
    public void onClick() {
        super.onClick();

        int i = getQsTile().getState();
        if (i == Tile.STATE_ACTIVE) {
            notificationEnabledPref.set(false);
        } else if (i == Tile.STATE_INACTIVE) {
            notificationEnabledPref.set(true);
            startNotificationService();

        }
    }

    @Override
    public void onStopListening() {
        if (notificationEnabledSubscription != null && !notificationEnabledSubscription.isUnsubscribed())
            notificationEnabledSubscription.unsubscribe();

        super.onStopListening();
    }

    @Override
    public void onDestroy() {
        notificationEnabledSubscription = null;
        notificationEnabledPref = null;

        super.onDestroy();
    }

    /**
     * Starts the {@link NotificationService}.
     */
    private void startNotificationService() {
        startService(new Intent(this, NotificationService.class));
    }
}
