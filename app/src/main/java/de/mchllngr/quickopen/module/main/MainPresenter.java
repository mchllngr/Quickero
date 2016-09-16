package de.mchllngr.quickopen.module.main;

import de.mchllngr.quickopen.base.BasePresenter;

/**
 * {@link com.hannesdorfmann.mosby.mvp.MvpPresenter} for the {@link MainActivity}
 *
 * @author Michael Langer (<a href="https://github.com/mchllngr" target="_blank">GitHub</a>)
 */
@SuppressWarnings("ConstantConditions")
public class MainPresenter extends BasePresenter<MainView> {

    @Override
    public void attachView(MainView view) {
        super.attachView(view);
        getApplicationComponent().inject(this);
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
    }
}
