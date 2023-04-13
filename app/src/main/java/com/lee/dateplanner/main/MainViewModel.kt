package com.lee.dateplanner.main

import android.app.Application
import com.lee.dateplanner.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(application: Application): BaseViewModel(application) {


}