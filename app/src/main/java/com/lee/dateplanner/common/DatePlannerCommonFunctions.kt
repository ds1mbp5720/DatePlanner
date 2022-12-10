package com.lee.dateplanner.common


import android.content.Context
import android.widget.Toast
import com.lee.dateplanner.map.adpter.POIWindowAdapter
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView


// toast 사용 목적 함수
fun toastMessage(message: String){
    Toast.makeText(DatePlannerApplication.getAppInstance(),message,Toast.LENGTH_SHORT).show()
}
// 시간 string format
fun timeStringFormat(hour: Int, minute: Int): String{
    return if(minute < 10){ // minute이 한자리일 경우 0* 으로 보이게 변경
        "$hour:0${minute}"
    } else "$hour:${minute}"
}
// 날짜 string format
fun dateStringFormat(month: Int, day: Int):String{
    return "${month+1}월 ${day}일"  // month 0월 부터 시작함
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