package de.mchllngr.quickero.base

import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import de.mchllngr.quickero.R

/**
 * Base-class for work concerning every [android.app.Activity].
 *
 * @param <V> view-interface for this activity
 * @param <P> presenter for this activity
 * */
abstract class BaseActivity<V : MvpView, P : MvpBasePresenter<V>> : MvpActivity<V, P>() {
    /**
     * Overrides [androidx.appcompat.app.AppCompatActivity.setSupportActionBar] to
     * allow setting the default title when called.
     *
     * @param toolbar toolbar to set
     */
    override fun setSupportActionBar(toolbar: Toolbar?) {
        super.setSupportActionBar(toolbar)
        setDefaultActionBarTitle()
    }

    /**
     * Sets the default title for the [androidx.appcompat.app.ActionBar].
     */
    private fun setDefaultActionBarTitle() {
        setActionBarTitle(R.string.app_name)
    }

    /**
     * Sets the title for the [androidx.appcompat.app.ActionBar] via the
     * given [StringRes].
     *
     *
     * If the [androidx.appcompat.app.ActionBar] is not set yet the function does nothing.
     *
     * @param titleResId [StringRes] for the title
     */
    fun setActionBarTitle(@StringRes titleResId: Int) {
        if (supportActionBar != null) supportActionBar?.setTitle(titleResId)
    }

    /**
     * Sets the visibility of the home-button (up-arrow).
     */
    fun setShowHomeButton(showHomeButton: Boolean) {
        if (supportActionBar != null) {
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(showHomeButton)
                setDisplayShowHomeEnabled(showHomeButton)
            }
        }
    }
}
