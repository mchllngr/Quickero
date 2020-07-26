package de.mchllngr.quickopen.base;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import de.mchllngr.quickopen.R;

/**
 * Base-class for work concerning every {@link android.app.Activity}.
 *
 * @param <V> view-interface for this activity
 * @param <P> presenter for this activity
 */
public abstract class BaseActivity<V extends MvpView, P extends MvpBasePresenter<V>> extends DebugBaseActivity<V, P> {

    /**
     * Overrides {@link androidx.appcompat.app.AppCompatActivity#setSupportActionBar(Toolbar)} to
     * allow setting the default title when called.
     *
     * @param toolbar toolbar to set
     */
    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        setDefaultActionBarTitle();
    }

    /**
     * Sets the default title for the {@link androidx.appcompat.app.ActionBar}.
     */
    private void setDefaultActionBarTitle() {
        setActionBarTitle(R.string.app_name);
    }

    /**
     * Sets the title for the {@link androidx.appcompat.app.ActionBar} via the
     * given {@link StringRes}.
     * <p>
     * If the {@link androidx.appcompat.app.ActionBar} is not set yet the function does nothing.
     *
     * @param titleResId {@link StringRes} for the title
     */
    public void setActionBarTitle(@StringRes int titleResId) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(titleResId);
    }

    /**
     * Sets the visibility of the home-button (up-arrow).
     */
    public void setShowHomeButton(boolean showHomeButton) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(showHomeButton);
            getSupportActionBar().setDisplayShowHomeEnabled(showHomeButton);
        }
    }
}
