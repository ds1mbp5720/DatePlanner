package com.lee.dateplanner.timetable

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lee.dateplanner.timetable.timesheet.TimeSheet
import com.lee.dateplanner.timetable.time.room.Timetable
import com.lee.dateplanner.timetable.time.room.TimetableDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TimetableRepository(application: Application) {
    private var timetableDao: TimetableDAO? // dao 호출
    private var coroutineScope = CoroutineScope(Dispatchers.IO)  // 입출력 코루틴 동작
    val allTimetables: LiveData<MutableList<Timetable>>? // room db 전체 저장
    var searchResults = MutableLiveData<MutableList<Timetable>>()  // 특정 timetable 반환

    init {
        val db: com.lee.dateplanner.timetable.time.room.TimeTableRoomDatabase = com.lee.dateplanner.timetable.time.room.TimeTableRoomDatabase.getDatabase(application)
        timetableDao = db.timetableDao()
        allTimetables = timetableDao?.getAllTimetable()
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
    fun updateTimetable(timesheetList: MutableList<TimeSheet>, id: Int){
        coroutineScope.launch(Dispatchers.IO){
            asyncUpdate(timesheetList,id)
        }
    }
    private fun asyncUpdate(timesheetList: MutableList<TimeSheet>,id:Int){
        timetableDao?.updateTimetable(timesheetList, id)
    }
    // timetable의 date 수정 함수
    fun updateDate(Date: String, id: Int){
        coroutineScope.launch(Dispatchers.IO){
            asyncUpdateDate(Date, id)
        }
    }
    private fun asyncUpdateDate(Date : String, id: Int){
        timetableDao?.updateDate(Date, id )
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
    private suspend fun asyncFind(id:Int): MutableList<Timetable>? =
        coroutineScope.async(Dispatchers.IO) {
            return@async timetableDao?.findTimetable(id)
        }.await()
}