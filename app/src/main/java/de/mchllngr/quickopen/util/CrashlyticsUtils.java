package de.mchllngr.quickopen.util;

import android.os.Build;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

public class CrashlyticsUtils {

    private CrashlyticsUtils() { /* private */ }

    public static void logStartApplicationEvent() {
        Answers.getInstance().logCustom(
                new CustomEvent("Started Application")
                        .putCustomAttribute("manufacturer", Build.MANUFACTURER)
                        .putCustomAttribute("model", Build.MODEL)
                        .putCustomAttribute("sdk", Integer.toString(Build.VERSION.SDK_INT))
        );
    }
}
