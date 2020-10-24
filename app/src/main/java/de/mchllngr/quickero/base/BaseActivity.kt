package de.mchllngr.quickero.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import de.mchllngr.quickero.extension.viewModelClass
import de.mchllngr.quickero.extension.viewModels

open class BaseActivity<B : ViewDataBinding, VM : BaseViewModel>(private val inflateBlock: () -> B) : AppCompatActivity() {

    protected val viewModel by viewModels<VM>(viewModelClass(1))
    protected var binding: B? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflateBlock().also {
            it.lifecycleOwner = this
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
