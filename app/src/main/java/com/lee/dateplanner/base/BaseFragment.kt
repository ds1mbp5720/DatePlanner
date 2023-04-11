package com.lee.dateplanner.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.lee.dateplanner.BR

abstract class BaseFragment<T : ViewDataBinding, E : BaseViewModel> : Fragment() {
    lateinit var dataBinding: T

    abstract val layoutId: Int
    abstract val viewModel: E

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        dataBinding.setVariable(BR.viewModel, viewModel)
        initObserve()
        initViews()
        return dataBinding.root
    }

    open fun initObserve(){
    }

    open fun initViews() = Unit
}