package de.mchllngr.quickopen.base;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;

/**
 * Base-class for work concerning every {@link MvpPresenter}.
 *
 * @param <V> view-interface for this fragment
 */
public class BasePresenter<V extends BaseView> extends MvpBasePresenter<V> {
}
