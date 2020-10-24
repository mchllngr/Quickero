package de.mchllngr.quickero.util

import android.animation.Animator

interface CustomAnimatorListener : Animator.AnimatorListener {

    @JvmDefault // TODO remove after #7
    override fun onAnimationStart(animation: Animator) = Unit

    @JvmDefault // TODO remove after #7
    override fun onAnimationEnd(animation: Animator) = Unit

    @JvmDefault // TODO remove after #7
    override fun onAnimationCancel(animation: Animator) = Unit

    @JvmDefault // TODO remove after #7
    override fun onAnimationRepeat(animation: Animator) = Unit
}
