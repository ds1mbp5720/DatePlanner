package com.lee.dateplanner.timetable.time.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

//date(일자), list<onetime> onetime은 room말고 그냥 dataclass?
@Dao
interface TimetableDAO {
    @Insert
    fun insertProduct(timetable: Timetable)

    // 계획 찾기
    @Query("SELECT * FROM timetable_tbl WHERE timeSheetList = :name")
    fun findProduct(name: String): List<Timetable>

    //삭제
    @Query("DELETE FROM timetable_tbl WHERE timeSheetList = :name")
    fun deleteProduct(name: String)

    // 전체 반환
    @Query("SELECT * FROM timetable_tbl")
    fun getAllProduct(): LiveData<List<Timetable>>
}