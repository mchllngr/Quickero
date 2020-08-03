package de.mchllngr.quickopen.base;

import android.app.Application;
import android.support.annotation.NonNull;

import timber.log.Timber;

/**
 * Base-class used for debug initializations.
 */
public abstract class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initTimber();
    }

    /**
     * Initialises {@link Timber} with debug configuration
     */
    private void initTimber() {
        Timber.plant(new Timber.DebugTree() {
            // Add the line number to the tag
            @Override
            protected String createStackElementTag(@NonNull StackTraceElement element) {
                return super.createStackElementTag(element) + '#' + element.getLineNumber();
            }
        });
    }
}
