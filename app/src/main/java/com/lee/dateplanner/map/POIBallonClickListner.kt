package com.lee.dateplanner.map

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

/**
 * poi 정보의 marker window 터치에 대한 이벤트 처리 class
 */
class POIBallonClickListner(context: Context?):MapView.POIItemEventListener {
    // 말풍선 클릭시 이벤트 정의 함수
    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?, buttonType: MapPOIItem.CalloutBalloonButtonType?) {
        Log.e(TAG,"말풍선 터치")

    }

    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {}
    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {}
    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {}
}