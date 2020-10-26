package de.mchllngr.quickero.module.main

import android.graphics.drawable.Drawable
import de.mchllngr.quickero.repository.application.Application

sealed class MainItem {

    object Empty : MainItem()

    data class Application(
        val packageName: String,
        val icon: Drawable,
        val name: CharSequence
    ) : MainItem() {

        // ignore icon, because it's always a new instance
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Application) return false

            if (packageName != other.packageName) return false
            if (name != other.name) return false

            return true
        }

        // ignore icon, because it's always a new instance
        override fun hashCode(): Int {
            var result = packageName.hashCode()
            result = 31 * result + name.hashCode()
            return result
        }
    }
}

fun Application.toItem() = MainItem.Application(packageName, icon, name)
