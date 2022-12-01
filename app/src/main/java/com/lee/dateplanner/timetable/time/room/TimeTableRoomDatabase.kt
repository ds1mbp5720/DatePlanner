package com.lee.dateplanner.timetable.time.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * room db 가져오는 역할
 */
@Database(entities = [(Timetable::class)], exportSchema = false, version = 1)
@TypeConverters(TimeSheetListConverters::class)
abstract class TimeTableRoomDatabase:RoomDatabase() {
    abstract fun timetableDao(): TimetableDAO
    companion object {
        private lateinit var INSTANCE: TimeTableRoomDatabase
        internal  fun getDatabase(context: Context): TimeTableRoomDatabase{
            if (!this::INSTANCE.isInitialized){
                synchronized(TimeTableRoomDatabase::class.java){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        TimeTableRoomDatabase::class.java,
                        "timetable_database"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}