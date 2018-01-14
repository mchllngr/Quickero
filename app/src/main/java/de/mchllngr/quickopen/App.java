package de.mchllngr.quickopen;

import android.support.v7.app.AppCompatDelegate;

import com.crashlytics.android.Crashlytics;

import de.mchllngr.quickopen.base.BaseApp;
import de.mchllngr.quickopen.util.SplashScreenHelper;
import io.fabric.sdk.android.Fabric;

/**
 * {@link App} for the {@link android.app.Application}
 */
public class App extends BaseApp {

    static {
        // Sets the default night mode to follow system.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());

        registerActivityLifecycleCallbacks(new SplashScreenHelper());
    }
}
