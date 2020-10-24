package de.mchllngr.quickero.extension

import androidx.lifecycle.ViewModel
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
// dirty hack to get generic type https://stackoverflow.com/a/1901275/719212
fun <VM : ViewModel> Any.viewModelClass(positionViewModelTypeInGenerics: Int = 0): KClass<VM> =
    ((javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[positionViewModelTypeInGenerics] as Class<VM>).kotlin
