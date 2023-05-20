package com.lee.dateplanner.timetable.insert.address

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isEmpty
import com.lee.dateplanner.R
import com.lee.dateplanner.base.BaseActivity
import com.lee.dateplanner.common.mapSetting
import com.lee.dateplanner.databinding.AddressMapActivityBinding
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class AddressMapActivity : BaseActivity<AddressMapActivityBinding,AddressMapViewModel>() {
    override val layoutId: Int = R.layout.address_map_activity
    override val viewModel: AddressMapViewModel by viewModels()
    private lateinit var mapView : MapView
    private var lat = 0.0
    private var lng = 0.0
    private val mapViewListener = object : MapView.MapViewEventListener {
        override fun onMapViewCenterPointMoved(mapView: MapView?, mapPoint: MapPoint) {
            lat = mapPoint.mapPointGeoCoord.latitude
            lng = mapPoint.mapPointGeoCoord.longitude
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
        insertMapSetting()
        dataBinding.mapView.postDelayed({
            if(intent.hasExtra("address")){
                lat = intent.getDoubleExtra("lat",0.0)
                lng = intent.getDoubleExtra("lng",0.0)
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
                    setResult(RESULT_OK, Intent().run {
                        putExtra("latitude",lat)
                        putExtra("longitude",lng)
                    })
                    finish()
                }
            }
        }
    }

    override fun initViews() {
        super.initViews()
    }
    private fun insertMapSetting(){
        mapSetting(mapView,this)
        mapView.setMapViewEventListener(mapViewListener)
    }

}