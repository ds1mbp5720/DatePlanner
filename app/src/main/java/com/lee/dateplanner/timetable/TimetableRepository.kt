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
        val db: com.lee.dateplanner.timetable.time.room.ProductRoomDatabase? = com.lee.dateplanner.timetable.time.room.ProductRoomDatabase.getDatabase(application)
        timetableDao = db?.productDao()
        alltimetables = timetableDao?.getAllProduct()
    }
    // data 입력 함수
    fun insertProduct(newTimetable: Timetable){
        coroutineScope.launch(Dispatchers.IO){
            asyncInsert(newTimetable)
        }
    }
    private fun asyncInsert(timetable: Timetable){
        timetableDao?.insertProduct(timetable)
    }
    fun deleteProduct(name: String){
        coroutineScope.launch(Dispatchers.IO){
            asyncDelete(name)
        }
    }
    private fun asyncDelete(name:String){
        timetableDao?.deleteProduct(name)
    }
    fun findProduct(name: String){
        coroutineScope.launch(Dispatchers.Main) {
            searchResults.value = asyncFind(name)
        }
    }
    private suspend fun asyncFind(name:String): List<Timetable>? =
        coroutineScope.async(Dispatchers.IO) {
            return@async timetableDao?.findProduct(name)
        }.await()
}