package de.mchllngr.quickero.util;

import android.animation.Animator;

public interface CustomAnimatorListener extends Animator.AnimatorListener {

    @Override
    default void onAnimationStart(Animator animation) { /* empty */ }

    @Override
    default void onAnimationEnd(Animator animation) { /* empty */ }

    @Override
    default void onAnimationCancel(Animator animation) { /* empty */ }

    @Override
    default void onAnimationRepeat(Animator animation) { /* empty */ }
}
