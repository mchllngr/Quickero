package de.mchllngr.quickopen.module.settings;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import de.mchllngr.quickopen.R;

/**
 * {@link android.preference.PreferenceFragment} for handling the settings-page.
 *
 * @author Michael Langer (<a href="https://github.com/mchllngr" target="_blank">GitHub</a>)
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    /**
     * Static factory method that initializes the {@link SettingsFragment} and returns it.
     *
     * @return {@link SettingsFragment}
     */
    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.prefs);
    }
}
