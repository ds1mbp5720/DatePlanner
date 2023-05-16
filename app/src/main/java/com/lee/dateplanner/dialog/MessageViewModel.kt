package com.lee.dateplanner.dialog

import android.app.Application
import androidx.lifecycle.LiveData
import com.lee.dateplanner.base.BaseViewModel
import com.lee.dateplanner.base.SingleLiveEvent

class MessageViewModel(application: Application) : BaseViewModel(application) {

    private val _leftClick = SingleLiveEvent<Unit>()
    val leftClick: LiveData<Unit> get() = _leftClick

    private val _rightClick = SingleLiveEvent<Unit>()
    val rightClick: LiveData<Unit> get() = _rightClick

    fun onLeftClick() {
        _leftClick.call()
    }

    fun onRightClick() {
        _rightClick.call()
    }

}