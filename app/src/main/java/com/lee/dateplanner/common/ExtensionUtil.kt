package com.lee.dateplanner.common

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat
import com.lee.dateplanner.festival.data.FestivalInfoData
import com.lee.dateplanner.timetable.time.room.Timetable


fun MutableList<FestivalInfoData.CulturalEventInfo.Row>.filterByTodayDate(): MutableList<FestivalInfoData.CulturalEventInfo.Row>{
    val festivalRowList = mutableListOf<FestivalInfoData.CulturalEventInfo.Row>()
    for(i in 0 until  this.size){
        val endDate = this[i].eNDDATE.filterFestivalDateInt()
        if(endDate >= getTodayDate()){
            festivalRowList.add(this[i])
        }
    }
    return festivalRowList
}

fun MutableList<FestivalInfoData.CulturalEventInfo.Row>.filterByDate(year: Int, month: Int, day: Int): MutableList<FestivalInfoData.CulturalEventInfo.Row>{
    val insertDate = filterInsertDateInt(year,month, day)
    val festivalRowList = mutableListOf<FestivalInfoData.CulturalEventInfo.Row>()
    for(i in 0 until  this.size){
        val startDate = this[i].sTRTDATE.filterFestivalDateInt()
        val endDate = this[i].eNDDATE.filterFestivalDateInt()
        if(insertDate in startDate..endDate)
            festivalRowList.add(this[i])
    }
    return festivalRowList
}
fun String.filterFestivalDateInt(): Int {
    val filterToList = this.split("-", " ") as MutableList<String>
    return (filterToList[0] + filterToList[1] + filterToList[2]).toInt()
}

fun Timetable.copyClipBoard():String {
    var schedule = ""
    this.timeSheetList.forEach{
        schedule = it.time + " : " + it.title + "\n"
    }
    return schedule
}

fun Context.copyToClipboard(text: String) {
    val clipboard =
        ContextCompat.getSystemService<ClipboardManager>(
            this,
            ClipboardManager::class.java
        )
    clipboard?.let {
        val clip =
            ClipData.newPlainText(text, text)
        it.setPrimaryClip(clip)
    }
}