package com.lee.dateplanner.poimap

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isEmpty
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lee.dateplanner.R
import com.lee.dateplanner.base.BaseFragment
import com.lee.dateplanner.common.*
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
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

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
    lateinit var selectMarker: MapPOIItem
    private var job : Job? = null
    lateinit var mapView :MapView
    private var reCreateMapFlag = false
    //lateinit var kakaoMapView :KakaoMapFragment // todo: 추후 사용 예정
    var selectMarkerPOIFragment = SelectMarkerPOIFragment()
    val behavior by lazy { BottomSheetBehavior.from(dataBinding.bottomPoiList) } // = BottomSheetBehavior.from(dataBinding.bottomPoiList)
    // 정보 리스트와 maker 연동 목적 map
    var markerResolver: MutableMap<POIData.Document,MapPOIItem> = HashMap()
    var markerResolver2: MutableMap<MapPOIItem,POIData.Document> = HashMap()
    private val mapViewListener = object :MapViewEventListener{
        override fun onMapViewInitialized(mapView: MapView?) {}
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
        override fun onMapViewMoveFinished(mapView: MapView?, mapPoint: MapPoint?) {
            centerLat = mapPoint?.mapPointGeoCoord?.latitude.toString()
            centerLgt = mapPoint?.mapPointGeoCoord?.longitude.toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        poiAdapter = POIRecyclerAdapter(this)
        mapView = MapView(this.activity)
        getFestivalPosition()
        selectMarker = festivalMarker
        dataBinding.infoMap.addView(mapView)
        //kakaoMapView = KakaoMapFragment().also { childFragmentManager.beginTransaction().add(R.id.info_map,it).addToBackStack("").commit() }
    }
    override fun onResume() {
        super.onResume()
        if(dataBinding.infoMap.isEmpty()){
            mapView = MapView(this.activity)
            getFestivalPosition()
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(centerLat.toDouble(),centerLgt.toDouble()),false)
            dataBinding.infoMap.addView(mapView)
            firstSettingPoiMapView(mapView)
            reCreateMapFlag = true
        }else{
            firstSettingPoiMapView(mapView)
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(festivalLat.toDouble(), festivalLgt.toDouble()), false) // map 중심점
        }
    }
    override fun onPause() {
        super.onPause()
        dataBinding.infoMap.removeView(mapView)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
        poiBalloonListener.job?.cancel()
        job?.cancel()
    }
    // 최초 fragment 실행시 MapView 추가 설정 함수
    private fun firstSettingPoiMapView(mapView: MapView){
        mapSetting(mapView, this@POIMapFragment.requireContext(),poiBalloonListener)
        mapView.setMapViewEventListener(mapViewListener)
        viewModel.getAllPoiFromViewModel(poiCategory, centerLat,centerLgt,1)
        dataBinding.btRestaurantBtn.unSelect()
        dataBinding.btCafeBtn.select()
        dataBinding.btEnjoyBtn .unSelect()
        mapView.addPOIItem(festivalMarker) // 행사위치 핑
    }
    // 전달받은 행사장 좌표값
    private fun getFestivalPosition(){
        festivalMarker = settingMarker(getString(R.string.festivalMarkerTitle),festivalLat.toDouble(),festivalLgt.toDouble(),false,MapPOIItem.MarkerType.RedPin)
    }
    // 전체 마커 map 표시 함수
    private fun displayPOI(data: POIData, map:MapView){
        with(map){
            if(data.documents.size < map.poiItems.size){
                for(i in map.poiItems.size-1 downTo 0){
                    if(map.poiItems[i].itemName != selectMarker.itemName){
                        map.removePOIItem(map.poiItems[i])
                    }
                }
            }
            addPOIItem(festivalMarker) // 카테고리 바꿀시 새로 생성할 행사위치 핑
            for(i in 0 until  data.documents.size){
                val document = data.documents[i]
                val marker = settingMarker(document.placeName,document.y.toDouble(),document.x.toDouble(),false,MapPOIItem.MarkerType.BluePin)
                addPOIItem(marker) // 현 마커 추가
                markerResolver[document] = marker
                markerResolver2[marker] = document
            }
            if(reCreateMapFlag){
                map.addPOIItem(selectMarker)
                map.selectPOIItem(selectMarker,false)
                reCreateMapFlag = false
            }
        }
    }

    override fun initObserve() {
        super.initObserve()
        viewModel.poiList.observe(this){
            with(dataBinding.poiInfoRecycler){
                run{
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
                    dataBinding.btRestaurantBtn.select()
                    dataBinding.btCafeBtn.unSelect()
                    dataBinding.btEnjoyBtn .unSelect()
                    viewModel.getAllPoiFromViewModel(poiCategory, centerLat, centerLgt,1)
                }
                POIViewModel.Event.Cafe -> {
                    poiCategory = PoiCategoryType.CAFE
                    dataBinding.btRestaurantBtn.unSelect()
                    dataBinding.btCafeBtn.select()
                    dataBinding.btEnjoyBtn .unSelect()
                    viewModel.getAllPoiFromViewModel(poiCategory, centerLat, centerLgt,1)
                }
                POIViewModel.Event.Enjoy -> {
                    poiCategory = PoiCategoryType.ENJOY
                    dataBinding.btRestaurantBtn.unSelect()
                    dataBinding.btCafeBtn.unSelect()
                    dataBinding.btEnjoyBtn .select()
                    viewModel.getAllPoiFromViewModel(poiCategory, centerLat, centerLgt,1)
                }
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner){
            Log.e(TAG,it)
        }
    }
    @Subscribe
    fun onEvent(event: FestivalInfoEventBus){
        festivalLat = event.latitude.toString()
        festivalLgt = event.longitude.toString()
        viewModel.getAllPoiFromViewModel(poiCategory, festivalLat,festivalLgt,1)
        festivalMarker = settingMarker(getString(R.string.festivalMarkerTitle),festivalLat.toDouble(),festivalLgt.toDouble(),false,MapPOIItem.MarkerType.RedPin)
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(festivalLat.toDouble(), festivalLgt.toDouble()), 3,false) // map 중심점
    }
    //Todo Eventbus 활용 data 받기
    object PoiCategoryType {
        const val CAFE = "CE7"
        const val RESTAURANT = "FD6"
        const val ENJOY = "CT1"
        const val ATTRACTIONS = "AT4"
        const val PARKING = "PK6"
        const val STAY = "AD5"
    }
}