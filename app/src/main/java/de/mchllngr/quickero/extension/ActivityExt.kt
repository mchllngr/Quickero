package de.mchllngr.quickero.extension

import android.app.Activity
import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.annotation.Px

val Activity.deviceScreenWidth: Int
    @Px get() = if (VERSION.SDK_INT >= VERSION_CODES.R) {
        val windowManager = createDisplayContext(display!!).getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.currentWindowMetrics.bounds.width()
    } else {
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION") windowManager.defaultDisplay.getMetrics(metrics)
        metrics.widthPixels
    }
