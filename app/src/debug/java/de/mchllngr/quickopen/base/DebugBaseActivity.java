package de.mchllngr.quickopen.base;

import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import androidx.annotation.LayoutRes;
import de.mchllngr.quickopen.util.debug.DebugDrawerHelper;
import io.palaima.debugdrawer.DebugDrawer;

/**
 * BaseDebug-class used for initialization of the {@link DebugDrawer}.
 */
public abstract class DebugBaseActivity<V extends MvpView, P extends MvpBasePresenter<V>> extends MvpActivity<V, P> {

    /**
     * {@link DebugDrawerHelper} reference to use.
     */
    private DebugDrawerHelper debugDrawerHelper;

    /**
     * Overrides {@link androidx.appcompat.app.AppCompatActivity#setContentView(int)} to
     * allow setting the {@link DebugDrawer} when called.
     *
     * @param layoutResID {@link LayoutRes} used for setting the ContentView
     */
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        setDebugDrawer();
    }

    /**
     * Overrides {@link androidx.appcompat.app.AppCompatActivity#setContentView(View)} to
     * allow setting the {@link DebugDrawer} when called.
     *
     * @param view {@link View} used for setting the ContentView
     */
    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setDebugDrawer();
    }

    /**
     * Overrides {@link androidx.appcompat.app.AppCompatActivity#setContentView(View, ViewGroup.LayoutParams)}
     * to allow setting the {@link DebugDrawer} when called.
     *
     * @param view {@link View} used for setting the ContentView
     * @param params {@link ViewGroup.LayoutParams} used for setting the ContentView
     */
    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        setDebugDrawer();
    }

    /**
     * Initialises the {@link DebugDrawer} and sets it.
     */
    private void setDebugDrawer() {
        if (debugDrawerHelper == null)
            debugDrawerHelper = new DebugDrawerHelper(this);
        debugDrawerHelper.initDebugDrawer();
    }

    @Override
    public void onBackPressed() {
        if (!debugDrawerHelper.closeDrawerIfOpened())
            super.onBackPressed();
    }
}
