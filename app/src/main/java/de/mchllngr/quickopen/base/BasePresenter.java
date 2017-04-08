package de.mchllngr.quickopen.base;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;

/**
 * Base-class for work concerning every {@link MvpPresenter}.
 *
 * @param <V> view-interface for this fragment
 * @author Michael Langer <a href="https://github.com/mchllngr" target="_blank">(GitHub)</a>
 */
public class BasePresenter<V extends BaseView> extends MvpBasePresenter<V> {
}
