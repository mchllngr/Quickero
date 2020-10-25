package de.mchllngr.quickero.module.main

import android.graphics.drawable.Drawable
import de.mchllngr.quickero.repository.application.Application

sealed class MainItem {

    object Empty : MainItem()

    data class Application(
        val packageName: String,
        val icon: Drawable,
        val name: CharSequence
    ) : MainItem()
}

fun Application.toItem() = MainItem.Application(packageName, icon, name)
