package com.lee.dateplanner.timetable.time.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TimetableDAO {
    @Insert
    fun insertTimetable(timetable: Timetable)

    @Update
    fun updateTimetable(timetable: Timetable)

    // 찾기
    @Query("SELECT * FROM timetable_tbl WHERE timetableId = :id")
    fun findTimetable(id: Int): List<Timetable>

    //삭제
    @Query("DELETE FROM timetable_tbl WHERE timetableId = :id")
    fun deleteTimetable(id: Int)

    // 전체 반환
    @Query("SELECT * FROM timetable_tbl")
    fun getAllTimetable(): LiveData<List<Timetable>>


}