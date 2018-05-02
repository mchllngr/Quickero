package de.mchllngr.quickopen.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import de.mchllngr.quickopen.BuildConfig;
import de.mchllngr.quickopen.R;
import timber.log.Timber;

public class FirebaseUtils {

    private static final String EVENT_CLICKED_APP_IN_NOTIFICATION = "clicked_app_in_notification";
    private static final String USER_PROPERTY_NOTIFICATION_ON_PREFS = "notification_on_prefs";
    private static final String USER_PROPERTY_NOTIFICATION_ON_SETTINGS = "notification_on_settings";
    private static final int DEFAULT_CACHE_EXPIRATION_SECONDS = 3600;

    private FirebaseUtils() { /* private */ }

    @NonNull
    private static FirebaseAnalytics getFirebaseAnalytics(@NonNull Context context) {
        return FirebaseAnalytics.getInstance(context);
    }

    @NonNull
    private static FirebaseRemoteConfig getFirebaseRemoteConfig() {
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        remoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build());
        remoteConfig.setDefaults(R.xml.remote_config_defaults);
        return remoteConfig;
    }

    public static void logStartApplicationEvent(@NonNull Context context) {
        getFirebaseAnalytics(context).logEvent(EVENT_CLICKED_APP_IN_NOTIFICATION, null);
        Timber.i("Logging event '" + EVENT_CLICKED_APP_IN_NOTIFICATION + "'");
    }

    public static void setUserPropertyNotificationsEnabledInPrefs(@NonNull Context context, boolean enabled) {
        getFirebaseAnalytics(context).setUserProperty(USER_PROPERTY_NOTIFICATION_ON_PREFS, Boolean.toString(enabled));
        Timber.i("Setting user property '" + USER_PROPERTY_NOTIFICATION_ON_PREFS + "' to '" + enabled + "'");
    }

    public static void setUserPropertyNotificationsEnabledInAndroidSettings(@NonNull Context context, boolean enabled) {
        getFirebaseAnalytics(context).setUserProperty(USER_PROPERTY_NOTIFICATION_ON_SETTINGS, Boolean.toString(enabled));
        Timber.i("Setting user property '" + USER_PROPERTY_NOTIFICATION_ON_SETTINGS + "' to '" + enabled + "'");
    }

    public static void fetchRemoteConfig(@Nullable OnSuccessListener<Void> listener) {
        FirebaseRemoteConfig remoteConfig = getFirebaseRemoteConfig();
        long cacheExpirationSeconds = remoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled() ? 0L : DEFAULT_CACHE_EXPIRATION_SECONDS;
        remoteConfig.fetch(cacheExpirationSeconds)
                .addOnSuccessListener(task -> {
                    remoteConfig.activateFetched();
                    if (listener != null) listener.onSuccess(null);
                });
    }

    public static long getLowestSupportedVersion() {
        return getFirebaseRemoteConfig().getLong("lowest_supported_version");
    }
}
