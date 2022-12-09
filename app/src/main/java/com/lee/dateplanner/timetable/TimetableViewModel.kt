package com.lee.dateplanner.timetable

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lee.dateplanner.timetable.timesheet.TimeSheet
import com.lee.dateplanner.timetable.time.room.Timetable

class TimetableViewModel(application: Application): AndroidViewModel(application) {
    private val repository: TimetableRepository = TimetableRepository(application)
    private val allTimetables: LiveData<MutableList<Timetable>>? = repository.allTimetables
    private val searchResults: MutableLiveData<MutableList<Timetable>> = repository.searchResults

    fun insertTimeTable(timetable: Timetable){
        repository.insertTimetable(timetable)
    }
    fun updateTimetable(timesheetList: MutableList<TimeSheet>, id: Int){
        repository.updateTimetable(timesheetList, id)
    }
    fun updateDate(Date: String, id:Int){
        repository.updateDate(Date, id)
    }

    fun findTimetable(id: Int){
        repository.findTimetable(id)
    }

    fun deleteTimetable(id: Int){
        repository.deleteTimetable(id)
    }

    fun getSearchResults(): MutableLiveData<MutableList<Timetable>> {
        return searchResults
    }
    fun getAllTimetables(): LiveData<MutableList<Timetable>>? {
        return allTimetables
    }
}