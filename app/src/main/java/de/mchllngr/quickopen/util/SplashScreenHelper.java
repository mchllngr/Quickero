package de.mchllngr.quickopen.util;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import de.mchllngr.quickopen.R;

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
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityStarted(Activity activity) { /* empty */ }

    @Override
    public void onActivityResumed(Activity activity) { /* empty */ }

    @Override
    public void onActivityPaused(Activity activity) { /* empty */ }

    @Override
    public void onActivityStopped(Activity activity) { /* empty */ }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) { /* empty */ }

    @Override
    public void onActivityDestroyed(Activity activity) { /* empty */ }
}

