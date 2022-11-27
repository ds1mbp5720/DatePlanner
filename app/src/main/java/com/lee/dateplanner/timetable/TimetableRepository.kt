package com.lee.dateplanner.timetable

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lee.dateplanner.timetable.time.room.Timetable
import com.lee.dateplanner.timetable.time.room.TimetableDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TimetableRepository(application: Application) {
    var searchResults = MutableLiveData<List<Timetable>>()
    private var timetableDao: TimetableDAO?
    private var coroutineScope = CoroutineScope(Dispatchers.IO)
    val alltimetables: LiveData<List<Timetable>>?

    init {
        val db: com.lee.dateplanner.timetable.time.room.TimeTableRoomDatabase? = com.lee.dateplanner.timetable.time.room.TimeTableRoomDatabase.getDatabase(application)
        timetableDao = db?.timetableDao()
        alltimetables = timetableDao?.getAllTimetable()
    }
    // data 입력 함수
    fun insertTimetable(newTimetable: Timetable){
        coroutineScope.launch(Dispatchers.IO){
            asyncInsert(newTimetable)
        }
    }
    private fun asyncInsert(timetable: Timetable){
        timetableDao?.insertTimetable(timetable)
    }
    fun deleteTimetable(name: String){
        coroutineScope.launch(Dispatchers.IO){
            asyncDelete(name)
        }
    }
    private fun asyncDelete(name:String){
        timetableDao?.deleteTimetable(name)
    }
    fun findTimetable(name: String){
        coroutineScope.launch(Dispatchers.Main) {
            searchResults.value = asyncFind(name)
        }
    }
    private suspend fun asyncFind(name:String): List<Timetable>? =
        coroutineScope.async(Dispatchers.IO) {
            return@async timetableDao?.findTimetable(name)
        }.await()
}