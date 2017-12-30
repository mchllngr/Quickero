package de.mchllngr.quickopen;

import android.support.v7.app.AppCompatDelegate;

import de.mchllngr.quickopen.base.BaseApp;

/**
 * {@link App} for the {@link android.app.Application}
 */
public class App extends BaseApp {

    static {
        // Sets the default night mode to follow system.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }
}
