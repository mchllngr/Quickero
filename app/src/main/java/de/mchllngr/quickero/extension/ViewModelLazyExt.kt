package de.mchllngr.quickero.extension

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import kotlin.reflect.KClass

/**
 * Makes [androidx.activity.ActivityViewModelLazy.viewModels] usable for base activities with generic [ViewModel]s.
 */
@Suppress("KDocUnresolvedReference")
@MainThread
fun <VM : ViewModel> ComponentActivity.viewModels(
    viewModelClazz: KClass<VM>,
    factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val factoryPromise = factoryProducer ?: {
        defaultViewModelProviderFactory
    }

    return ViewModelLazy(viewModelClazz, { viewModelStore }, factoryPromise)
}
