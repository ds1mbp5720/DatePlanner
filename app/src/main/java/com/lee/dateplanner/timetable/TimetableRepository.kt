package com.lee.dateplanner.timetable

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lee.dateplanner.timetable.time.room.Timetable
import com.lee.dateplanner.timetable.time.room.TimetableDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TimetableRepository(application: Application) {
    private var timetableDao: TimetableDAO?
    private var coroutineScope = CoroutineScope(Dispatchers.IO)
    val alltimetables: LiveData<List<Timetable>>?
    var searchResults = MutableLiveData<List<Timetable>>()

    init {
        val db: com.lee.dateplanner.timetable.time.room.TimeTableRoomDatabase? = com.lee.dateplanner.timetable.time.room.TimeTableRoomDatabase.getDatabase(application)
        timetableDao = db?.timetableDao()
        alltimetables = timetableDao?.getAllTimetable()
    }
    // 일일 timetable 추가
    fun insertTimetable(newTimetable: Timetable){
        coroutineScope.launch(Dispatchers.IO){
            asyncInsert(newTimetable)
        }
    }
    private fun asyncInsert(timetable: Timetable){
        timetableDao?.insertTimetable(timetable)
    }

    //일일 timetable 수정
    fun updateTimetable(newTimetable: Timetable){
        coroutineScope.launch(Dispatchers.IO){
            asyncUpdate(newTimetable)
        }
    }
    private fun asyncUpdate(timetable: Timetable){
        timetableDao?.updateTimetable(timetable)
    }

    // 일일 timetable 삭제
    fun deleteTimetable(id: Int){
        coroutineScope.launch(Dispatchers.IO){
            asyncDelete(id)
        }
    }
    private fun asyncDelete(id:Int){
        timetableDao?.deleteTimetable(id)
    }

    // 일일 timetable 탐색
    fun findTimetable(id: Int){
        coroutineScope.launch(Dispatchers.Main) {
            searchResults.value = asyncFind(id)
        }
    }
    private suspend fun asyncFind(id:Int): List<Timetable>? =
        coroutineScope.async(Dispatchers.IO) {
            return@async timetableDao?.findTimetable(id)
        }.await()
}