package de.mchllngr.quickopen.module.main;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.mchllngr.quickopen.R;
import de.mchllngr.quickopen.base.BaseActivity;
import de.mchllngr.quickopen.service.NotificationService;
import de.mchllngr.quickopen.util.GsonPreferenceAdapter;

/**
 * {@link android.app.Activity} for handling the selection of applications.
 *
 * @author Michael Langer (<a href="https://github.com/mchllngr" target="_blank">GitHub</a>)
 */
public class MainActivity extends BaseActivity<MainView, MainPresenter> implements MainView {

    /**
     * {@link Toolbar} for this {@link android.app.Activity}.
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    /**
     * {@link android.support.design.widget.FloatingActionButton} for adding items.
     */
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        testSetPackageVisibilityPrefs();
        testSetPackagePriorityPrefs();
        testSetPackageNamePrefs();

        startNotificationService();
    }

    @NonNull
    @Override
    public FragmentActivity getActivity() {
        return this;
    }

    /**
     * Starts the {@link NotificationService}.
     */
    private void startNotificationService() {
        startService(new Intent(this, NotificationService.class));
    }

    /**
     * TODO remove
     */
    private void testSetPackageVisibilityPrefs() {
        RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(
                PreferenceManager.getDefaultSharedPreferences(this));

        Preference<Integer> packageVisibilityPref = rxSharedPreferences.getInteger(
                getString(R.string.pref_notification_visibility));

        packageVisibilityPref.set(NotificationCompat.VISIBILITY_PUBLIC);
    }

    /**
     * TODO remove
     */
    private void testSetPackagePriorityPrefs() {
        RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(
                PreferenceManager.getDefaultSharedPreferences(this));

        Preference<Integer> packagePriorityPref = rxSharedPreferences.getInteger(
                getString(R.string.pref_notification_priority));

        packagePriorityPref.set(Notification.PRIORITY_MAX);
    }

    /**
     * TODO remove
     */
    private void testSetPackageNamePrefs() {
        RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(
                PreferenceManager.getDefaultSharedPreferences(this));

        GsonPreferenceAdapter<List> adapter = new GsonPreferenceAdapter<>(new Gson(), List.class);
        Preference<List> packageNamesPref = rxSharedPreferences.getObject(
                getString(R.string.pref_package_names), null, adapter);

        List<String> packageNamesData = new ArrayList<>();
        packageNamesData.add("com.imgur.mobile");
        packageNamesData.add("com.novagecko.memedroid");
        packageNamesData.add("com.google.android.talk");
        packageNamesData.add("com.facebook.orca");
        packageNamesData.add("com.whatsapp");
        packageNamesData.add("com.google.android.apps.maps");
        packageNamesData.add("com.mobitobi.android.gentlealarm");

        packageNamesPref.set(packageNamesData);
    }
}
