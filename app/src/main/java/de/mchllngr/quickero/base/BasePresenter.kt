package de.mchllngr.quickero.base

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter

/**
 * Base-class for work concerning every [MvpPresenter].
 *
 * @param <V> view-interface for this fragment
 */
open class BasePresenter<V : BaseView> : MvpBasePresenter<V>()
