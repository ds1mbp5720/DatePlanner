package com.lee.dateplanner.common

import android.content.Context
import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.lee.dateplanner.festival.data.FestivalInfoData
import com.naver.maps.map.overlay.Marker


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

fun String.idxToKakaoHash(): Int {
    if (this.isNotEmpty()) {
        var code = when {
            this[0] == 'O' -> return ("1" + this.drop(7)).toInt()
            this[0] == 'R' -> "2"
            this[0] == 'S' -> "3"
            else -> return -1
        }
        code += this.drop(1).dropLast(5)
        code += this.substring(this.length - 5, this.length).replace("0", "")
        return code.toInt()
    }
    return -1
}