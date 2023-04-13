package com.lee.dateplanner.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.lee.dateplanner.BR

abstract class BaseActivity<T : ViewDataBinding, E : BaseViewModel> : AppCompatActivity() {
    lateinit var dataBinding: T

    abstract val layoutId: Int
    abstract val viewModel: E

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
   
        initObserve()
        initDataBinding()
        initViews()
    }

    private fun initDataBinding() {
        dataBinding = DataBindingUtil.setContentView(this, layoutId)
        dataBinding.lifecycleOwner = this
        dataBinding.setVariable(BR.viewModel, viewModel)


    }
    open fun initObserve() {

    }
    open fun initViews() = Unit
}