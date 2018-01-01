package de.mchllngr.quickopen.util;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Helper-class for starting a {@link Fragment}.
 */
public class FragmentStarter {

    /**
     * Starts a {@link Fragment}.
     *
     * @param fragmentManager   {@link FragmentManager} to use
     * @param fragment          {@link Fragment} to start
     * @param fragmentContainer {@link IdRes} of a container to load the {@link Fragment} into
     */
    public static void startFragment(@NonNull FragmentManager fragmentManager, Fragment fragment, @IdRes int fragmentContainer) {
        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(fragmentContainer, fragment);
            transaction.commit();
        }
    }
}
