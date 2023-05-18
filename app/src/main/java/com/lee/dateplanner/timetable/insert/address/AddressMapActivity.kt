package com.lee.dateplanner.timetable.insert.address

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isEmpty
import com.lee.dateplanner.R
import com.lee.dateplanner.base.BaseActivity
import com.lee.dateplanner.common.mapSetting
import com.lee.dateplanner.common.settingMarker
import com.lee.dateplanner.databinding.AddressMapActivityBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class AddressMapActivity : BaseActivity<AddressMapActivityBinding,AddressMapViewModel>() {
    override val layoutId: Int = R.layout.address_map_activity
    override val viewModel: AddressMapViewModel by viewModels()
    private lateinit var mapView : MapView
    private val mapViewListener = object : MapView.MapViewEventListener {
        override fun onMapViewCenterPointMoved(mapView: MapView?, mapPoint: MapPoint?) {
            //todo 중앙값 받아오기
        }
        override fun onMapViewInitialized(mapView: MapView?) {}
        override fun onMapViewDragEnded(mapView: MapView, mapPoint: MapPoint?) {}
        override fun onMapViewZoomLevelChanged(mapView: MapView?, p1: Int) {}
        override fun onMapViewSingleTapped(mapView: MapView?, mapPoint: MapPoint?) {}
        override fun onMapViewDoubleTapped(mapView: MapView?, mapPoint: MapPoint?) {}
        override fun onMapViewLongPressed(mapView: MapView?, mapPoint: MapPoint?) {}
        override fun onMapViewDragStarted(mapView: MapView?, mapPoint: MapPoint?) {}
        override fun onMapViewMoveFinished(mapView: MapView?, mapPoint: MapPoint?) {}
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapView = MapView(this)
        dataBinding.mapView.addView(mapView)
        dataBinding.mapView.postDelayed({
            if(intent.hasExtra("address")){
                val lat = intent.getDoubleExtra("lat",0.0)
                val lng = intent.getDoubleExtra("lng",0.0)
                if(lat > 0.0 && lng > 0.0)
                    mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lat,lng),false)
            }
        },100)
    }

    override fun onResume() {
        super.onResume()
        if(dataBinding.mapView.isEmpty()){
            mapView = MapView(this)
            dataBinding.mapView.addView(mapView)
            insertMapSetting()
        }
    }

    override fun onPause() {
        super.onPause()
        dataBinding.mapView.removeView(mapView)
    }

    override fun initObserve() {
        super.initObserve()
        viewModel.event.observe(this){
            when(it){
                AddressMapViewModel.Event.Close -> finish()
                AddressMapViewModel.Event.Save -> {
                    //todo lat,lng 전달 EventBus사용하기
                }
            }
        }
    }

    override fun initViews() {
        super.initViews()
    }
    private fun insertMapSetting(){
        mapSetting(mapView,this)

    }

}