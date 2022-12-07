package com.lee.dateplanner.map.adpter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.lee.dateplanner.R
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapPOIItem

/**
 * 마커 선택시 나타날 info window(말풍선) 정의 함수, 내 시간계획의 마커 class 별도 생성하기
  */
class POIWindowAdapter(context: Context):CalloutBalloonAdapter {
    // xml view 연결
    private var mCalloutBalloon: View = LayoutInflater.from(context).inflate(R.layout.poi_window, null)

    override fun getCalloutBalloon(poiItem: MapPOIItem): View {
        (mCalloutBalloon.findViewById<View>(R.id.window_title) as TextView).text = poiItem.itemName
        return mCalloutBalloon
    }

    override fun getPressedCalloutBalloon(poiItem: MapPOIItem): View? {
        return null
    }
}