package de.mchllngr.quickopen.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.mchllngr.quickopen.R;
import de.mchllngr.quickopen.base.BaseActivity;
import de.mchllngr.quickopen.model.ApplicationModel;
import de.mchllngr.quickopen.module.settings.SettingsActivity;
import de.mchllngr.quickopen.service.NotificationService;

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
     * {@link android.support.v7.widget.RecyclerView} for showing list of items.
     */
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    /**
     * {@link android.support.design.widget.FloatingActionButton} for adding items.
     */
    @BindView(R.id.fab)
    FloatingActionButton fab;

    /**
     * {@link MainAdapter} for updating shown items in {@code recyclerView}.
     */
    private MainAdapter adapter;

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

        initRecyclerView();

        // TODO show with plus-icon to add entries
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        startNotificationService();
    }

    /**
     * Initialises the {@code recyclerView}.
     */
    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // TODO remove temp
        ArrayList<ApplicationModel> temp = new ArrayList<>();
        temp.add(ApplicationModel.getApplicationModelForPackageName(this, "com.imgur.mobile"));
        temp.add(ApplicationModel.getApplicationModelForPackageName(this, "com.novagecko.memedroid"));
        temp.add(ApplicationModel.getApplicationModelForPackageName(this, "com.google.android.talk"));
        temp.add(ApplicationModel.getApplicationModelForPackageName(this, "com.facebook.orca"));
        temp.add(ApplicationModel.getApplicationModelForPackageName(this, "com.whatsapp"));
        temp.add(ApplicationModel.getApplicationModelForPackageName(this, "com.google.android.apps.maps"));
        temp.add(ApplicationModel.getApplicationModelForPackageName(this, "com.mobitobi.android.gentlealarm"));
        temp.add(ApplicationModel.getApplicationModelForPackageName(this, "com.imgur.mobile"));
        temp.add(ApplicationModel.getApplicationModelForPackageName(this, "com.novagecko.memedroid"));
        temp.add(ApplicationModel.getApplicationModelForPackageName(this, "com.google.android.talk"));
        temp.add(ApplicationModel.getApplicationModelForPackageName(this, "com.facebook.orca"));
        temp.add(ApplicationModel.getApplicationModelForPackageName(this, "com.whatsapp"));
        temp.add(ApplicationModel.getApplicationModelForPackageName(this, "com.google.android.apps.maps"));
        temp.add(ApplicationModel.getApplicationModelForPackageName(this, "com.mobitobi.android.gentlealarm"));
        temp.add(ApplicationModel.getApplicationModelForPackageName(this, "com.imgur.mobile"));
        temp.add(ApplicationModel.getApplicationModelForPackageName(this, "com.novagecko.memedroid"));
        temp.add(ApplicationModel.getApplicationModelForPackageName(this, "com.google.android.talk"));
        temp.add(ApplicationModel.getApplicationModelForPackageName(this, "com.facebook.orca"));
        temp.add(ApplicationModel.getApplicationModelForPackageName(this, "com.whatsapp"));
        temp.add(ApplicationModel.getApplicationModelForPackageName(this, "com.google.android.apps.maps"));
        temp.add(ApplicationModel.getApplicationModelForPackageName(this, "com.mobitobi.android.gentlealarm"));
        adapter = new MainAdapter(temp);

//        adapter = new MainAdapter(new ArrayList<ApplicationModel>());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                SettingsActivity.start(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

//    private void testSetPackageNamePrefs() {
//        RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(
//                PreferenceManager.getDefaultSharedPreferences(this));
//
//        GsonPreferenceAdapter<List> adapter = new GsonPreferenceAdapter<>(new Gson(), List.class);
//        Preference<List> packageNamesPref = rxSharedPreferences.getObject(
//                getString(R.string.pref_package_names), null, adapter);
//
//        List<String> packageNamesData = new ArrayList<>();
//        packageNamesData.add("com.imgur.mobile");
//        packageNamesData.add("com.novagecko.memedroid");
//        packageNamesData.add("com.google.android.talk");
//        packageNamesData.add("com.facebook.orca");
//        packageNamesData.add("com.whatsapp");
//        packageNamesData.add("com.google.android.apps.maps");
//        packageNamesData.add("com.mobitobi.android.gentlealarm");
//
//        packageNamesPref.set(packageNamesData);
//    }
}
