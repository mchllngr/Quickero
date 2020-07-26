package de.mchllngr.quickopen.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FirebaseUtils {

    private static final String EVENT_CLICKED_APP_IN_NOTIFICATION = "clicked_app_in_notification";
    private static final String USER_PROPERTY_NOTIFICATION_ON_PREFS = "notification_on_prefs";
    private static final String USER_PROPERTY_NOTIFICATION_ON_SETTINGS = "notification_on_settings";
    private static final String CONFIG_LOWEST_SUPPORTED_VERSION = "lowest_supported_version";
    private static final int DEFAULT_CACHE_EXPIRATION_SECONDS = 60 * 60; // 1 hour

    private FirebaseUtils() { /* private */ }

//    @NonNull
//    private static FirebaseAnalytics getFirebaseAnalytics(@NonNull Context context) {
//        return FirebaseAnalytics.getInstance(context);
//    }

//    @NonNull
//    private static FirebaseRemoteConfig getFirebaseRemoteConfig() {
//        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
//        remoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder()
//                .setDeveloperModeEnabled(BuildConfig.DEBUG)
//                .build());
//        remoteConfig.setDefaults(R.xml.remote_config_defaults);
//        return remoteConfig;
//    }

    public static void logStartApplicationEvent(@NonNull Context context) {
//        getFirebaseAnalytics(context).logEvent(EVENT_CLICKED_APP_IN_NOTIFICATION, null);
//        Timber.i("Logging event '" + EVENT_CLICKED_APP_IN_NOTIFICATION + "'");
    }

    public static void setUserPropertyNotificationsEnabledInPrefs(@NonNull Context context, boolean enabled) {
//        getFirebaseAnalytics(context).setUserProperty(USER_PROPERTY_NOTIFICATION_ON_PREFS, Boolean.toString(enabled));
//        Timber.i("Setting user property '" + USER_PROPERTY_NOTIFICATION_ON_PREFS + "' to '" + enabled + "'");
    }

    public static void setUserPropertyNotificationsEnabledInAndroidSettings(@NonNull Context context, boolean enabled) {
//        getFirebaseAnalytics(context).setUserProperty(USER_PROPERTY_NOTIFICATION_ON_SETTINGS, Boolean.toString(enabled));
//        Timber.i("Setting user property '" + USER_PROPERTY_NOTIFICATION_ON_SETTINGS + "' to '" + enabled + "'");
    }

//    private static void fetchRemoteConfig(@Nullable OnCompleteListener<Void> listener) {
//        FirebaseRemoteConfig remoteConfig = getFirebaseRemoteConfig();
//        long cacheExpirationSeconds = remoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled() ? 0L : DEFAULT_CACHE_EXPIRATION_SECONDS;
//        remoteConfig.fetch(cacheExpirationSeconds)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        remoteConfig.activateFetched();
//                    }
//                    if (listener != null) listener.onComplete(task);
//                });
//    }

    public static boolean isVersionSupportedFromCache() {
//        boolean supported = BuildConfig.VERSION_CODE >= getFirebaseRemoteConfig().getLong(CONFIG_LOWEST_SUPPORTED_VERSION);
//        Timber.v("Version '" + BuildConfig.VERSION_CODE + "' is " + (supported ? "" : "not ") + "supported");
//        return supported;
        return true;
    }

    public static void isVersionSupported(@Nullable VersionSupportedResultListener listener) {
//        fetchRemoteConfig(ignored -> {
        if (listener != null) listener.onVersionSupportedResult(isVersionSupportedFromCache());
//        });
    }

    public interface VersionSupportedResultListener {
        void onVersionSupportedResult(boolean supported);
    }
}
