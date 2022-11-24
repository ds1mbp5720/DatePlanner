package com.lee.dateplanner.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class POIViewModelFactory (private var repository:POIRepository):ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(POIViewModel::class.java)){
            POIViewModel(repository) as T
        }else{
            throw IllegalArgumentException("해당 뷰모델 못찾을수 없습니다.")
        }
    }
}