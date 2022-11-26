package com.lee.dateplanner.timetable.time.room

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.lee.dateplanner.timetable.onetime.TimeSheet

@Entity(tableName = "timetable_tbl")
class Timetable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "timetableId") // 구분 id
    var id: Int = 0

    @ColumnInfo(name = "timeSheetList") // 시간별 계획 리스트
    var timeSheetList: List<TimeSheet>? = null

    @ColumnInfo(name = "date") // 계획 날짜
    var date: String = ""

    constructor(){}
    @Ignore
    constructor(id: Int, timeSheetList: List<TimeSheet>, date: String){
        this.id = id
        this.timeSheetList = timeSheetList
        this.date = date
    }

    @Ignore
    constructor(timeSheetList: List<TimeSheet>, date: String){
        this.timeSheetList = timeSheetList
        this.date = date
    }
}