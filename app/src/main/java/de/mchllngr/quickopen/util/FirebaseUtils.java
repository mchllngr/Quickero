package de.mchllngr.quickopen.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.analytics.FirebaseAnalytics;

import timber.log.Timber;

public class FirebaseUtils {

    private static final String EVENT_CLICKED_APP_IN_NOTIFICATION = "clicked_app_in_notification";
    private static final String USER_PROPERTY_NOTIFICATION_ON_PREFS = "notification_on_prefs";
    private static final String USER_PROPERTY_NOTIFICATION_ON_SETTINGS = "notification_on_settings";

    private FirebaseUtils() { /* private */ }

    @NonNull
    private static FirebaseAnalytics getFirebaseAnalytics(@NonNull Context context) {
        return FirebaseAnalytics.getInstance(context);
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
}
