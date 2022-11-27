package com.lee.dateplanner.timetable.time.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TimetableDAO {
    @Insert
    fun insertTimetable(timetable: Timetable)

    // 계획 찾기
    @Query("SELECT * FROM timetable_tbl WHERE timeSheetList = :name")
    fun findTimetable(name: String): List<Timetable>

    //삭제
    @Query("DELETE FROM timetable_tbl WHERE timeSheetList = :name")
    fun deleteTimetable(name: String)

    // 전체 반환
    @Query("SELECT * FROM timetable_tbl")
    fun getAllTimetable(): LiveData<List<Timetable>>
}