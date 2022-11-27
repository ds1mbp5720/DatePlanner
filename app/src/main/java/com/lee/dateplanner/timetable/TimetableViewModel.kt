package com.lee.dateplanner.timetable

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lee.dateplanner.timetable.time.room.Timetable

class TimetableViewModel(application: Application): AndroidViewModel(application) {
    private val repository: TimetableRepository = TimetableRepository(application)
    private val allTimetables: LiveData<List<Timetable>>? = repository.alltimetables
    private val searchResults: MutableLiveData<List<Timetable>> = repository.searchResults

    fun insertTimeTable(timetable: Timetable){
        repository.insertTimetable(timetable)
    }
    fun findTimetable(name: String){
        repository.findTimetable(name)
    }
    fun deleteTimetable(name: String){
        repository.deleteTimetable(name)
    }
    fun getSearchResults(): MutableLiveData<List<Timetable>> {
        return searchResults
    }
    fun getAllTimetables(): LiveData<List<Timetable>>?{
        return allTimetables
    }
}