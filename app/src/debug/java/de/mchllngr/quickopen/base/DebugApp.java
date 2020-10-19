package de.mchllngr.quickopen.base;

import android.app.Application;

import com.pandulapeter.beagle.Beagle;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import de.mchllngr.quickopen.util.DebugDrawer;
import timber.log.Timber;

/**
 * Base-class used for debug initializations.
 */
public abstract class DebugApp extends Application {

    private final DebugDrawer debugDrawer = new DebugDrawer(this);

    @Override
    public void onCreate() {
        super.onCreate();

        initTimber();
        debugDrawer.init();
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

            @Override
            protected void log(int priority, String tag, @NotNull String message, Throwable t) {
                Beagle.INSTANCE.log(message, tag, null);
                super.log(priority, tag, message, t);
            }
        });
    }
}
