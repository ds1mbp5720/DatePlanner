package com.lee.dateplanner.map

import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.*
import com.naver.maps.map.widget.CompassView

class MapController (private val naverMap: NaverMap) : MapInterface, Overlay.OnClickListener {

    private val mapComponentClick = hashMapOf<Int, () -> (Unit)>()
    private val mapMarkerList = hashMapOf<Int, Marker>()
    private val mapPolylineList = hashMapOf<Int, PathOverlay>()

    init {
        naverMap.mapType = NaverMap.MapType.Basic
       // naverMap.isNightModeEnabled =  // Todo 야간모드 시 추가
        naverMap.moveCamera(CameraUpdate.zoomTo(14.0))
    }

    override fun clear() {
        mapMarkerList.forEach { it.value.map = null }
        mapPolylineList.forEach { it.value.map = null }
        mapMarkerList.clear()
        mapPolylineList.clear()
        mapComponentClick.clear()
    }

    override fun addMarker(item: MapData.MarkerItem) {
        if (mapMarkerList.containsKey(item.hash)) {
            mapMarkerList[item.hash]?.apply {
                position = LatLng(item.mapPoint.first, item.mapPoint.second)
            }
        } else {
            val marker = Marker()
            marker.onClickListener = this
            marker.position = LatLng(item.mapPoint.first, item.mapPoint.second)
            marker.tag = item.hash
            marker.setCaptionAligns(Align.Top)
            marker.isHideCollidedMarkers = false
            marker.map = naverMap
            mapMarkerList[item.hash] = marker
        }
    }

    override fun addMarker(item: MapData.MarkerItem, action: () -> (Unit)) {
        mapComponentClick[item.hash] = action
        addMarker(item)
    }

    fun changMarkersNameVisible() {
        mapMarkerList.values.forEach {
            it.captionText = ""
        }
    }

    override fun removeMarker(hash: Int) {
        if (mapMarkerList.containsKey(hash)) mapMarkerList.remove(hash)?.apply {
            map = null
        }
    }

    override fun addPolyLine(item: MapData.PolylineItem) {
        if (mapPolylineList.containsKey(item.hash)) {
            mapPolylineList[item.hash]?.apply {
                coords = listOf(LatLng(item.startPoint.first, item.startPoint.second), LatLng(item.endPoint.first, item.endPoint.second))
                color = item.color
            }
        } else {
            val path = PathOverlay()
            path.coords = listOf(LatLng(item.startPoint.first, item.startPoint.second), LatLng(item.endPoint.first, item.endPoint.second))
            path.color = item.color
            path.tag = item.hash
            path.onClickListener = this
            path.map = naverMap
            mapPolylineList[item.hash] = path
        }
    }

    override fun addPolyLine(item: MapData.PolylineItem, action: () -> (Unit)) {
        mapComponentClick[item.hash] = action
        addPolyLine(item)
    }

    override fun removePolyLine(hash: Int) {
        if (mapPolylineList.containsKey(hash)) mapPolylineList.remove(hash)?.map = null
    }

    override fun onClick(overlay: Overlay): Boolean {
        if (mapComponentClick.containsKey(overlay.tag)) mapComponentClick[overlay.tag]?.invoke()
        return true
    }

    fun setCompass(view: CompassView) {
        naverMap.uiSettings.isCompassEnabled = false
        view.map = naverMap
    }

}