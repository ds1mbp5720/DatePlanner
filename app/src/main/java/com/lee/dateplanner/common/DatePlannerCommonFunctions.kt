package com.lee.dateplanner.common


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateFormat
import android.widget.Toast
import com.lee.dateplanner.map.adpter.POIWindowAdapter
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.text.SimpleDateFormat
import java.util.*

// toast 사용 목적 함수
fun toastMessage(message: String){
    Toast.makeText(DatePlannerApplication.getAppInstance(),message,Toast.LENGTH_SHORT).show()
}
// 시간 string format
fun timeStringFormat(hour: Int, minute: Int): String{
    val hourString: String = if(hour < 10){ "0$hour"
    }else "$hour"
    val minuteString: String = if(minute < 10){ ":0${minute}"
    } else ":${minute}"
    return hourString + minuteString
}
// 날짜 string format
fun dateStringFormat(month: Int, day: Int):String{
    return "${month+1}월 ${day}일"  // month 0월 부터 시작함
}
//오늘 날짜 int 반환(연,월,일)
@SuppressLint("SimpleDateFormat")
fun getTodayDate(): Int{
    val dataFormat = SimpleDateFormat("yyyy-MM-dd")
    val date = dataFormat.format(System.currentTimeMillis()).toString().split("-")
    return (date[0] + date[1] + date[2]).toInt()
}
// 맵 설정 함수
fun mapSetting(map: MapView, context: Context, poiEventListener: MapView.POIItemEventListener){
    with(map){
        mapType = MapView.MapType.Standard
        setZoomLevel(3, true)
        zoomIn(true)
        zoomOut(true)
        setPOIItemEventListener(poiEventListener)
        setCalloutBalloonAdapter(POIWindowAdapter(context))
    }
}
// 카테고리 text 수정 함수
fun setCategoryTextFilter(category: String):String{
    val categoryList: MutableList<String> = category.split(">") as MutableList<String>
    return categoryList[categoryList.size-1]
}
// 마커 설정 함수
fun settingMarker(title: String,latitude: Double, longitude: Double, drag: Boolean, type:MapPOIItem.MarkerType):MapPOIItem{
    val marker = MapPOIItem()
    with(marker){
        tag = 0
        markerType = type
        showAnimationType = MapPOIItem.ShowAnimationType.NoAnimation
        mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude)
        itemName = title
        isDraggable = drag
    }
    return marker
}
fun makeDatePickerDialog(context: Context, listener: DatePickerDialog.OnDateSetListener){
    val cal = Calendar.getInstance()
    DatePickerDialog(context,listener,cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
}
fun makeTimePickerDialog(context: Context, listener: TimePickerDialog.OnTimeSetListener){
    val cal = Calendar.getInstance()
    TimePickerDialog(context,listener,cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), DateFormat.is24HourFormat(context)).show()
}
fun filterInsertDateInt(year: Int, month: Int, day: Int): Int{
    val setMonth = if(month<10){ "0$month" }else month.toString()
    val setDay = if(day<10){ "0$day" }else day.toString()
    return (year.toString() + setMonth + setDay).toInt()
}