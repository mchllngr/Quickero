package de.mchllngr.quickopen;

import androidx.appcompat.app.AppCompatDelegate;
import de.mchllngr.quickopen.base.BaseApp;
import de.mchllngr.quickopen.util.SplashScreenHelper;

/**
 * {@link App} for the {@link android.app.Application}
 */
public class App extends BaseApp {

    static {
        // Sets the default night mode to follow system.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(new SplashScreenHelper());
    }
}
