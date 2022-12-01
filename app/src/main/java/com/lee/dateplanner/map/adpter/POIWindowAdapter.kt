package com.lee.dateplanner.map.adpter

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.lee.dateplanner.R
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapPOIItem

/**
 * 마커 선택시 나타날 info window(말풍선) 정의 함수
  */
class POIWindowAdapter(context: Context):CalloutBalloonAdapter {
    // xml view 연결
    private var mCalloutBalloon: View = LayoutInflater.from(context).inflate(R.layout.poi_window, null)

    override fun getCalloutBalloon(poiItem: MapPOIItem): View {
        (mCalloutBalloon.findViewById<View>(R.id.window_title) as TextView).text = poiItem.itemName
        mCalloutBalloon.findViewById<ImageButton>(R.id.poiaddbtn).setOnClickListener {
            Log.e(TAG,"추가 클릭")
        }
        return mCalloutBalloon
    }

    override fun getPressedCalloutBalloon(p0: MapPOIItem?): View? {

        return null
    }
}