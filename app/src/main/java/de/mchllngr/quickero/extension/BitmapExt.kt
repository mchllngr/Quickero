package de.mchllngr.quickero.extension

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

/** Create a [Bitmap] from this [Drawable]. */
fun Drawable.toBitmap(): Bitmap {
    if (this is BitmapDrawable && bitmap != null) return bitmap

    return if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
        // Single color bitmap will be created of 1x1 pixel
        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    } else {
        Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    }.apply {
        val canvas = Canvas(this)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
    }
}
