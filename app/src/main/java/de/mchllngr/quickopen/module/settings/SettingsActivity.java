package de.mchllngr.quickopen.module.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.mchllngr.quickopen.R;
import de.mchllngr.quickopen.base.BaseActivity;
import de.mchllngr.quickopen.base.BasePresenter;
import de.mchllngr.quickopen.base.BaseView;
import de.mchllngr.quickopen.util.FragmentStarter;

/**
 * {@link android.app.Activity} for handling the {@link SettingsFragment}.
 *
 * @author Michael Langer (<a href="https://github.com/mchllngr" target="_blank">GitHub</a>)
 */
public class SettingsActivity extends BaseActivity<BaseView, BasePresenter<BaseView>>
        implements BaseView {

    /**
     * {@link Toolbar} for this {@link android.app.Activity}.
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    /**
     * Static factory method that initializes and starts the {@link SettingsActivity}.
     */
    public static void start(Context context) {
        Intent starter = new Intent(context, SettingsActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setShowHomeButton(true);

        FragmentStarter.startFragment(getSupportFragmentManager(),
                SettingsFragment.newInstance(),
                R.id.fragment_container);
    }

    @NonNull
    @Override
    public BasePresenter<BaseView> createPresenter() {
        return new BasePresenter<>();
    }

    @Override
    public FragmentActivity getActivity() {
        return this;
    }
}
