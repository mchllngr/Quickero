package de.mchllngr.quickopen.util.debug;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindString;
import butterknife.ButterKnife;
import de.mchllngr.quickopen.R;
import io.palaima.debugdrawer.DebugDrawer;
import io.palaima.debugdrawer.actions.ActionsModule;
import io.palaima.debugdrawer.actions.SpinnerAction;
import io.palaima.debugdrawer.commons.BuildModule;
import io.palaima.debugdrawer.commons.DeviceModule;
import io.palaima.debugdrawer.commons.NetworkModule;
import io.palaima.debugdrawer.commons.SettingsModule;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_AUTO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static androidx.appcompat.app.AppCompatDelegate.NightMode;

/**
 * Helper-class for easier use with {@link DebugDrawer}.
 */
public class DebugDrawerHelper {

    /**
     * {@link AppCompatActivity} used for initialising.
     */
    private final AppCompatActivity activity;
    /**
     * {@link String} used for initialising the NightMode-{@link ActionsModule}.
     */
    @BindString(R.string.debug_night_mode_select) String debugNightModeSelect;
    /**
     * {@link String} used for initialising the NightMode-{@link ActionsModule}.
     */
    @BindString(R.string.debug_night_mode_yes) String debugNightModeYes;
    /**
     * {@link String} used for initialising the NightMode-{@link ActionsModule}.
     */
    @BindString(R.string.debug_night_mode_no) String debugNightModeNo;
    /**
     * {@link String} used for initialising the NightMode-{@link ActionsModule}.
     */
    @BindString(R.string.debug_night_mode_auto) String debugNightModeAuto;
    /**
     * {@link String} used for initialising the NightMode-{@link ActionsModule}.
     */
    @BindString(R.string.debug_night_mode_follow_system) String debugNightModeFollowSystem;
    /**
     * {@link DebugDrawer} reference to use.
     */
    private DebugDrawer debugDrawer;

    /**
     * Constructor with {@link AppCompatActivity} used for initialising.
     *
     * @param activity {@link AppCompatActivity} used for initialising
     */
    public DebugDrawerHelper(@NonNull AppCompatActivity activity) {
        this.activity = activity;
        ButterKnife.bind(this, activity);
    }

    /**
     * Initialises the {@link DebugDrawer}.
     */
    public void initDebugDrawer() {
        debugDrawer = new DebugDrawer.Builder(activity)
                .modules(
                        new ActionsModule(getNightModeActionsModule()),
                        new NetworkModule(),
                        new BuildModule(),
                        new DeviceModule(),
                        new SettingsModule()
                )
                .withTheme(R.style.AppTheme)
                .build();
    }

    /**
     * Returns the {@link ActionsModule} for selecting the {@link NightMode}
     *
     * @return {@link ActionsModule} for selecting the {@link NightMode}
     */
    private SpinnerAction getNightModeActionsModule() {
        return new SpinnerAction<>(
                Arrays.asList(
                        debugNightModeSelect,
                        debugNightModeYes,
                        debugNightModeNo,
                        debugNightModeAuto,
                        debugNightModeFollowSystem
                ),
                value -> {
                    int selectedMode = MODE_NIGHT_FOLLOW_SYSTEM;

                    if (value.equals(debugNightModeYes))
                        selectedMode = MODE_NIGHT_YES;
                    else if (value.equals(debugNightModeNo))
                        selectedMode = MODE_NIGHT_NO;
                    else if (value.equals(debugNightModeAuto))
                        selectedMode = MODE_NIGHT_AUTO;

                    activity.getDelegate().setLocalNightMode(selectedMode);
                    activity.recreate();
                }
        );
    }

    /**
     * Closes the {@link DebugDrawer} if it's opened.
     *
     * @return true if the {@link DebugDrawer} was closed, false otherwise
     */
    public boolean closeDrawerIfOpened() {
        if (debugDrawer.isDrawerOpen()) {
            debugDrawer.closeDrawer();
            return true;
        }
        return false;
    }
}
