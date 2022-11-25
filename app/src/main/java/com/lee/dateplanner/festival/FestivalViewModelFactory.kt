package com.lee.dateplanner.festival

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lee.dateplanner.map.POIRepository
import com.lee.dateplanner.map.POIViewModel

@Suppress("UNCHECKED_CAST")
class FestivalViewModelFactory(private var repository: FestivalRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(FestivalViewModel::class.java)){
            FestivalViewModel(repository) as T
        }else{
            throw IllegalArgumentException("해당 뷰모델 못찾을수 없습니다.")
        }
    }
}