package com.lee.dateplanner.poimap

import android.content.ContentValues.TAG
import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

/**
 * poi 정보의 marker window 터치에 대한 이벤트 처리 class
 */
class POIEventClickListener(private val owner: POIMapFragment = POIMapFragment()):MapView.POIItemEventListener {
    var job : Job? = null // cancel 해야함
    // 말풍선 클릭시 이벤트 정의 함수
    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?, buttonType: MapPOIItem.CalloutBalloonButtonType?) {}

    override fun onPOIItemSelected(p0: MapView?, poiItem: MapPOIItem?) {
        sendSelectPoiInfo(poiItem)
    }
    @Deprecated("Deprecated in Java")
    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {}

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
        if (p2 != null) {
            Log.e(TAG,"이동한 마커 좌표: ${p2.mapPointGeoCoord.latitude} ${p2.mapPointGeoCoord.longitude}")
        }
    }

    //선택한 마커의 정보 전달 및 selectMarkerPOIFragment 호출 함수
    private fun sendSelectPoiInfo(poiItem: MapPOIItem?){
        val info = owner.markerResolver2[poiItem]

        if (info != null) {
            with(info){
                job = CoroutineScope(Dispatchers.Main).launch(exceptionHandler) {
                    owner.childFragmentManager.setFragmentResult("poiKey", bundleOf("placeName" to placeName, "addressName" to addressName,
                        "phone" to phone, "category" to categoryName, "placeUrl" to placeUrl, "longitude" to x, "latitude" to y)
                    )
                    owner.childFragmentManager.beginTransaction()
                        .replace(com.lee.dateplanner.R.id.selected_marker_info,owner.selectMarkerPOIFragment).addToBackStack(null).commit()
                }
            }
        }
    }

    val errorMessage = MutableLiveData<String>()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("코루틴내 예외: ${throwable.localizedMessage}")
    }
    private fun onError(message: String){
        errorMessage.postValue(message)
    }
}