package com.lee.dateplanner.timetable.time.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [(Timetable::class)], exportSchema = false, version = 1)
abstract class ProductRoomDatabase:RoomDatabase() {
    abstract fun productDao(): TimetableDAO
    companion object {
        private lateinit var INSTANCE: ProductRoomDatabase
        internal  fun getDatabase(context: Context): ProductRoomDatabase{
            if (!this::INSTANCE.isInitialized){
                synchronized(ProductRoomDatabase::class.java){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        ProductRoomDatabase::class.java,
                        "timetable_database"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}