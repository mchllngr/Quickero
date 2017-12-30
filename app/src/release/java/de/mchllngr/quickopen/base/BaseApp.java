package de.mchllngr.quickopen.base;

import android.app.Application;

import de.mchllngr.quickopen.util.timber.ReleaseTree;
import timber.log.Timber;

/**
 * Base-class used for initialising {@link Timber}.
 */
public abstract class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initTimber();
    }

    /**
     * Initialises {@link Timber} with release configuration
     */
    private void initTimber() {
        Timber.plant(new ReleaseTree());
    }
}
