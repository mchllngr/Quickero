package de.mchllngr.quickero.util;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import de.mchllngr.quickero.R;
import timber.log.Timber;

/**
 * http://blog.davidmedenjak.com/android/2017/09/02/splash-screens.html
 */
public class SplashScreenHelper implements Application.ActivityLifecycleCallbacks {

    private static final String KEY_THEME = "theme";

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        // apply the actual theme given by meta-data
        try {
            ActivityInfo activityInfo = activity.getPackageManager().getActivityInfo(activity.getComponentName(), PackageManager.GET_META_DATA);

            int theme = R.style.AppTheme;
            if (activityInfo.metaData != null) {
                theme = activityInfo.metaData.getInt(KEY_THEME, R.style.AppTheme);
            }

            activity.setTheme(theme);
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e);
        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) { /* empty */ }

    @Override
    public void onActivityResumed(@NonNull Activity activity) { /* empty */ }

    @Override
    public void onActivityPaused(@NonNull Activity activity) { /* empty */ }

    @Override
    public void onActivityStopped(@NonNull Activity activity) { /* empty */ }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) { /* empty */ }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) { /* empty */ }
}

