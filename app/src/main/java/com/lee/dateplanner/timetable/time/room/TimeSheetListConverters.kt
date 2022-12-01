package com.lee.dateplanner.timetable.time.room

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.lee.dateplanner.timetable.onetime.TimeSheet

/**
 * room db에 timesheet list 저장용 형변환 변환 class
  */
class TimeSheetListConverters {
    @TypeConverter
    fun listToJson(value: List<TimeSheet>):String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String): List<TimeSheet>?{
        return Gson().fromJson(value,Array<TimeSheet>::class.java)?.toList()
    }
}