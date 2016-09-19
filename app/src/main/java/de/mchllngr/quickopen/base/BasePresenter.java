package de.mchllngr.quickopen.base;

import android.support.annotation.Nullable;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;

import de.mchllngr.quickopen.App;
import de.mchllngr.quickopen.injection.ApplicationComponent;

/**
 * Base-class for work concerning every {@link MvpPresenter}.
 *
 * @param <V> view-interface for this fragment
 * @author Michael Langer <a href="https://github.com/mchllngr" target="_blank">(GitHub)</a>
 */
public class BasePresenter<V extends BaseView> extends MvpBasePresenter<V> {

    /**
     * Gets the dagger-component for the whole application.
     *
     * @return dagger-component from the {@link App}
     */
    @Nullable
    @SuppressWarnings("ConstantConditions")
    protected ApplicationComponent getApplicationComponent() {
        App app;
        if (isViewAttached() && getView().getActivity() != null &&
                (app = (App) getView().getActivity().getApplication()) != null)
            return app.getApplicationComponent();

        return null;
    }
}
