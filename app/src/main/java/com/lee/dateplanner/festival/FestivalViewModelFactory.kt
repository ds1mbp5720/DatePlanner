package com.lee.dateplanner.festival

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lee.dateplanner.R

@Suppress("UNCHECKED_CAST")
class FestivalViewModelFactory(private var repository: FestivalRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(FestivalViewModel::class.java)){
            FestivalViewModel(repository) as T
        }else{
            throw IllegalArgumentException(R.string.notFindViewModel.toString())
        }
    }
}