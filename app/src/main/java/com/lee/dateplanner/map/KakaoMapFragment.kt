package com.lee.dateplanner.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.lee.dateplanner.R
import com.lee.dateplanner.base.BaseFragment
import com.lee.dateplanner.databinding.MapFragmentBinding
import com.naver.maps.map.MapFragment
import net.daum.mf.map.api.MapView

class KakaoMapFragment: BaseFragment<MapFragmentBinding,MapViewModel>(), MapInterface {
    override val layoutId: Int = R.layout.map_fragment
    override val viewModel: MapViewModel by viewModels()
    private lateinit var mapView: MapView
    private lateinit var mapController: KakaoMapController
    private var mapClickEvent: (() -> (Unit))? = null
    private var centerStart = Pair(0.0, 0.0)
    private var centerEnd = Pair(0.0, 0.0)
    private var centerLat = 0.0
    private var centerLng = 0.0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            mapView = MapView(activity).apply {
                //setMapViewEventListener(kakaoClickEvent)
            }
            mapController = KakaoMapController(mapView as MapView)
            dataBinding.mapView.addView(mapView as MapView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setMapClickEvent(action: () -> Unit) {
        mapClickEvent = action
    }

    override fun addMarker(item: MapData.MarkerItem) {
        mapController.addMarker(item)
    }

    override fun addMarker(item: MapData.MarkerItem, action: () -> Unit) {
        mapController.addMarker(item,action)
    }

    override fun removeMarker(hash: Int) {
        mapController.removeMarker(hash)
    }

    override fun removePolyLine(hash: Int) {
        mapController.removePolyLine(hash)
    }

    override fun addPolyLine(item: MapData.PolylineItem) {
        mapController.addPolyLine(item)
    }

    override fun addPolyLine(item: MapData.PolylineItem, action: () -> Unit) {
        mapController.addPolyLine(item, action)
    }

    override fun clear() {
        mapController.clear()
    }

}