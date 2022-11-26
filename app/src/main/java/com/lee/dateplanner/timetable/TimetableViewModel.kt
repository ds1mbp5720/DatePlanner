package com.lee.dateplanner.timetable

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lee.dateplanner.festival.FestivalRepository
import com.lee.dateplanner.timetable.time.room.Timetable

class TimetableViewModel(private var repository: TimetableRepository): ViewModel() {
    private val allProducts: LiveData<List<Timetable>>? = repository.alltimetables
    private val searchResults: MutableLiveData<List<Timetable>> = repository.searchResults

    fun insertProduct(product: Timetable){
        repository.insertProduct(product)
    }
    fun findProduct(name: String){
        repository.findProduct(name)
    }
    fun deleteProduct(name: String){
        repository.deleteProduct(name)
    }
    fun getSearchResults(): MutableLiveData<List<Timetable>> {
        return searchResults
    }
    fun getAllProducts(): LiveData<List<Timetable>>?{
        return allProducts
    }
}