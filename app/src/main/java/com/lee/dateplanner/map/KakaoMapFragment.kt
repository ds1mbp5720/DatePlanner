package com.lee.dateplanner.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isEmpty
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.lee.dateplanner.R
import com.lee.dateplanner.base.BaseFragment
import com.lee.dateplanner.databinding.MapFragmentBinding
import com.naver.maps.map.MapFragment
import net.daum.mf.map.api.MapPoint
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
    private val kakaoClickEvent by lazy {
        object : MapView.MapViewEventListener{
            override fun onMapViewInitialized(p0: MapView?) {

            }
            override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {

            }
            override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {}
            override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {}
            override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {}
            override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {}
            override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {}
            override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {}

            override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {}
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lifecycleObserver = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                if(dataBinding.mapView.isEmpty()){
                    mapView = MapView(activity).apply {
                        setMapViewEventListener(kakaoClickEvent)
                    }
                }
            }
            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                dataBinding.mapView.removeView(mapView)
                mapView.onSurfaceDestroyed()
            }
        }
        if(parentFragment != null) parentFragment?.lifecycle?.addObserver(lifecycleObserver)
        else activity?.lifecycle?.addObserver(lifecycleObserver)
    }
    fun setMapEventListener(listener: MapView.MapViewEventListener){
        mapView.setMapViewEventListener(listener)
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