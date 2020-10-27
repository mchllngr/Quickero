package de.mchllngr.quickero.repository.application

import android.graphics.drawable.Drawable

data class Application(
    val packageName: String,
    val icon: Drawable,
    val name: CharSequence
)
