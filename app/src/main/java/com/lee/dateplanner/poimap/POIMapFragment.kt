package com.lee.dateplanner.poimap

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar.LayoutParams
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lee.dateplanner.R
import com.lee.dateplanner.base.BaseFragment
import com.lee.dateplanner.common.mapSetting
import com.lee.dateplanner.common.settingMarker
import com.lee.dateplanner.databinding.PoiMapFragmentLayoutBinding
import com.lee.dateplanner.poimap.adpter.POIRecyclerAdapter
import com.lee.dateplanner.poimap.data.POIData
import com.lee.dateplanner.poimap.select.SelectMarkerPOIFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import net.daum.mf.map.api.MapView.MapViewEventListener

/**
 * 주변 상권정보 카테고리별 제공 fragment
 */
@AndroidEntryPoint
class POIMapFragment : BaseFragment<PoiMapFragmentLayoutBinding, POIViewModel>(){
    companion object{
        fun newInstance() = POIMapFragment()
    }
    override val layoutId: Int = R.layout.poi_map_fragment_layout
    override val viewModel: POIViewModel by viewModels()
    private lateinit var poiAdapter : POIRecyclerAdapter
    private val poiBalloonListener = POIEventClickListener(this) // info window 터치 객체
    private var poiCategory: String = "CE7" // category 저장 변수
    private var festivalLat: String = "37.5143225723" // 행사장 좌표
    private var centerLat: String = "37.5143225723" // 행사장 좌표
    private var festivalLgt: String = "127.062831022" // 행사장 좌표
    private var centerLgt: String = "127.062831022" // 행사장 좌표
    private lateinit var festivalMarker: MapPOIItem
    private var job : Job? = null
    lateinit var mapView :MapView
    var selectMarkerPOIFragment = SelectMarkerPOIFragment()
    // 정보 리스트와 maker 연동 목적 map
    var markerResolver: MutableMap<POIData.Document,MapPOIItem> = HashMap()
    var markerResolver2: MutableMap<MapPOIItem,POIData.Document> = HashMap()
    private val mapViewListener = object :MapViewEventListener{
        override fun onMapViewInitialized(mapView: MapView?) {
            centerLat = festivalLat
            centerLgt = festivalLgt
        }
        override fun onMapViewDragEnded(mapView: MapView, mapPoint: MapPoint?) {
            centerLat = mapView.mapCenterPoint.mapPointGeoCoord.latitude.toString()
            centerLgt = mapView.mapCenterPoint.mapPointGeoCoord.longitude.toString()
            viewModel.getAllPoiFromViewModel(poiCategory, centerLat,centerLgt,1)
        }
        override fun onMapViewCenterPointMoved(mapView: MapView?, mapPoint: MapPoint?) {}
        override fun onMapViewZoomLevelChanged(mapView: MapView?, p1: Int) {}
        override fun onMapViewSingleTapped(mapView: MapView?, mapPoint: MapPoint?) {}
        override fun onMapViewDoubleTapped(mapView: MapView?, mapPoint: MapPoint?) {}
        override fun onMapViewLongPressed(mapView: MapView?, mapPoint: MapPoint?) {}
        override fun onMapViewDragStarted(mapView: MapView?, mapPoint: MapPoint?) {}
        override fun onMapViewMoveFinished(mapView: MapView?, mapPoint: MapPoint?) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        poiAdapter = POIRecyclerAdapter(this)
        mapView = MapView(this.activity)
        getFestivalPosition()
        dataBinding.infoMap.addView(mapView)
        bottomSheetDownToBackKey()
        viewModel.getAllPoiFromViewModel(poiCategory, festivalLat,festivalLgt,1)
    }
    override fun onResume() {
        super.onResume()
        festivalMarker = settingMarker(getString(R.string.festivalMarkerTitle),festivalLat.toDouble(),festivalLgt.toDouble(),false,MapPOIItem.MarkerType.RedPin)
        Log.e("","지도 재생성 여부 ${dataBinding.infoMap.isEmpty()}")
        if(dataBinding.infoMap.isEmpty()){
            dataBinding.infoMap.addView(mapView)
        }else{
            firstSettingPoiMapView(mapView)
        }
    }
    override fun onPause() {
        super.onPause()
        //Todo 지도 지우기
        dataBinding.infoMap.removeView(mapView)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        poiBalloonListener.job?.cancel()
        job?.cancel()
    }
    // 최초 fragment 실행시 MapView 추가 설정 함수
    private fun firstSettingPoiMapView(mapView: MapView){
        mapSetting(mapView, this@POIMapFragment.requireContext(),poiBalloonListener)
        mapView.setMapViewEventListener(mapViewListener)
        viewModel.getAllPoiFromViewModel(poiCategory, festivalLat,festivalLgt,1)
        mapView.addPOIItem(festivalMarker) // 행사위치 핑
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(festivalLat.toDouble(), festivalLgt.toDouble()), false) // map 중심점
    }
    private fun createNewMapView(): MapView{
        val poiMap = MapView(this.activity)
        val layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        with(poiMap){
            poiMap.layoutParams = layoutParams
            id=R.id.info_map
            mapSetting(this, this@POIMapFragment.requireContext(),poiBalloonListener)
            addPOIItem(festivalMarker) // 새로 생성한 map 행사위치 핑
            setMapViewEventListener(mapViewListener)
        }
        return poiMap
    }
    // 전달받은 행사장 좌표값
    private fun getFestivalPosition(){
        setFragmentResultListener("positionKey"){ _, bundle ->
            festivalLat = bundle.getDouble("latitude").toString()
            festivalLgt = bundle.getDouble("longitude").toString()
        }
        festivalMarker = settingMarker(getString(R.string.festivalMarkerTitle),festivalLat.toDouble(),festivalLgt.toDouble(),false,MapPOIItem.MarkerType.RedPin)
    }
    // 전체 마커 map 표시 함수
    private fun displayPOI(data: POIData, map:MapView){
        with(map){
            removeAllPOIItems() // 기존 마커들 제거
            addPOIItem(festivalMarker) // 카테고리 바꿀시 새로 생성할 행사위치 핑
            for(i in 0 until  data.documents.size){
                val document = data.documents[i]
                val marker = settingMarker(document.placeName,document.y.toDouble(),document.x.toDouble(),false,MapPOIItem.MarkerType.BluePin)
                addPOIItem(marker) // 현 마커 추가
                markerResolver[document] = marker
                markerResolver2[marker] = document
            }
        }
    }

    private fun bottomSheetDownToBackKey(){
        val behavior = BottomSheetBehavior.from(dataBinding.bottomPoiList)
        activity?.onBackPressedDispatcher?.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }else{
                    activity!!.finish()
                }
            }
        })
    }

    override fun initObserve() {
        super.initObserve()
        viewModel.poiList.observe(this){
            with(dataBinding.poiInfoRecycler){
                run{
                    //poiAdapter = POIRecyclerAdapter(this@POIMapFragment)
                    poiAdapter.setPoiItem(it.documents)
                    job = poiAdapter.job
                    adapter = poiAdapter
                }
            }
            displayPOI(it,mapView)
            dataBinding.poiProgressBar.visibility = View.GONE
        }
        viewModel.eventClick.observe(this){
            when(it){
                POIViewModel.Event.Restaurant -> {
                    poiCategory = PoiCategoryType.RESTAURANT
                    viewModel.getAllPoiFromViewModel(poiCategory, centerLat, centerLgt,1)
                }
                POIViewModel.Event.Cafe -> {
                    poiCategory = PoiCategoryType.CAFE
                    viewModel.getAllPoiFromViewModel(poiCategory, centerLat, centerLgt,1)
                }
                POIViewModel.Event.Enjoy -> {
                    poiCategory = PoiCategoryType.ENJOY
                    viewModel.getAllPoiFromViewModel(poiCategory, centerLat, centerLgt,1)
                }
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner){
            Log.e(TAG,it)
        }
    }
    object PoiCategoryType {
        const val CAFE = "CE7"
        const val RESTAURANT = "FD6"
        const val ENJOY = "CT1"
        const val ATTRACTIONS = "AT4"
        const val PARKING = "PK6"
        const val STAY = "AD5"
    }
}