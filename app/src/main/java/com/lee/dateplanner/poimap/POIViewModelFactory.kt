package com.lee.dateplanner.poimap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lee.dateplanner.R

@Suppress("UNCHECKED_CAST")
class POIViewModelFactory (private var repository:POIRepository):ViewModelProvider.Factory{

    /*override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(POIViewModel::class.java)){
            POIViewModel(repository) as T
        }else{
            throw IllegalArgumentException(R.string.notFindViewModel.toString())
        }
    }*/
}