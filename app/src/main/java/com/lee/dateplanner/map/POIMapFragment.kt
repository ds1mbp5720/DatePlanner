package com.lee.dateplanner.map

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar.LayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.lee.dateplanner.R
import com.lee.dateplanner.databinding.AroundinfoMapFragmentLayoutBinding
import com.lee.dateplanner.map.adpter.POIRecyclerAdapter
import com.lee.dateplanner.map.adpter.POIWindowAdapter
import com.lee.dateplanner.map.data.POIData
import com.lee.dateplanner.map.network.POIRetrofitService
import com.lee.dateplanner.map.select.SelectMarkerPOIFragment
import kotlinx.coroutines.Job
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

/**
 * 주변 상권정보 카테고리별 제공 fragment
 */
class POIMapFragment:Fragment(){
    companion object{
        fun newInstance() = POIMapFragment()
    }
    lateinit var binding: AroundinfoMapFragmentLayoutBinding
    private lateinit var viewModel: POIViewModel
    private val poiBalloonListener = POIEventClickListener(this.context,this) // info window 터치 객체
    private var poiCategory: String = "CE7" // category 저장 변수
    private var festivalLat: String = "37.5143225723" // 행사장 좌표
    private var festivalLgt: String = "127.062831022" // 행사장 좌표
    private var job : Job? = null
    private lateinit var poiwindow : POIWindowAdapter
    var selectMarkerPOIFragment = SelectMarkerPOIFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AroundinfoMapFragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        binding.root.removeView(binding.root.findViewById(R.id.info_map))
    }

    override fun onResume() {
        super.onResume()
        if(binding.root.findViewById<MapView>(R.id.info_map) == null){
            binding.root.addView(createNewMap(),0)
        }
    }

    private fun createNewMap(): MapView{
        var poiMap = MapView(this.activity)
        val layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        with(poiMap){
            poiMap.layoutParams = layoutParams
            id=R.id.info_map
            mapSetting(this)
            observerSetup(viewModel,this)
        }
        return poiMap
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, POIViewModelFactory(
            POIRepository(POIRetrofitService.getInstance())
        )).get(POIViewModel::class.java)
        getFestivalPosition()
        mapSetting(binding.infoMap) // 기본 kakao map 설정
        observerSetup(viewModel,binding.infoMap)
        setCategoryBtn() // 상단 카테고리 버튼
        viewModel.getAllPoiFromViewModel(poiCategory, festivalLat, festivalLgt)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        poiBalloonListener.job?.cancel()
        job?.cancel()
    }

    // 옵저버 세팅
    private fun observerSetup(viewModel: POIViewModel, map: MapView){
        viewModel.poiList.observe(viewLifecycleOwner){
            with(binding.poiInfoRecycler){
                run{
                    val poiAdapter = POIRecyclerAdapter(this@POIMapFragment,it)
                    job = poiAdapter.job
                    adapter = poiAdapter
                    poiAdapter
                }
            }
            displayPOI(it,map)
            binding.poiProgressBar.visibility = View.GONE
        }
        viewModel.errorMessage.observe(viewLifecycleOwner){
            Log.e(TAG,it)
        }
    }
    // 전달받은 행사장 좌표값
    private fun getFestivalPosition(){
        setFragmentResultListener("positionKey"){ requestKey, bundle ->
            festivalLat = bundle.getDouble("latitude").toString()
            festivalLgt = bundle.getDouble("longitude").toString()
            // 내부에서만 값이 실시간 반영이 되므로 retrofit 함수는 여기서 실행
            viewModel.getAllPoiFromViewModel(poiCategory, festivalLat, festivalLgt)
        }
    }
    //카카오 지도 설정
    private fun mapSetting(map: MapView) {
        with(map) {
            mapType = MapView.MapType.Standard
            setZoomLevel(3, true)
            zoomIn(true)
            zoomOut(true)
            setPOIItemEventListener(poiBalloonListener)
            poiwindow = POIWindowAdapter(this.context)
            setCalloutBalloonAdapter(poiwindow)
            addPOIItem(settingFestivalMarker()) // 행사위치 핑
        }
    }
    //선택한 행사장 위치 Maker 생성 함수
    private fun settingFestivalMarker():MapPOIItem{
        val marker = MapPOIItem()
        with(marker){
            tag = 0
            markerType = MapPOIItem.MarkerType.RedPin // 마커 색
            showAnimationType = MapPOIItem.ShowAnimationType.NoAnimation
            mapPoint = MapPoint.mapPointWithGeoCoord(festivalLat.toDouble(), festivalLgt.toDouble()) // poi 장소 좌표
            itemName = "행사 장소" // 장소명
            isDraggable = true
        }
        return marker
    }
    // 정보 리스트와 maker 연동 목적 map
    var markerResolver: MutableMap<POIData.Document,MapPOIItem> = HashMap()
    var markerResolver2: MutableMap<MapPOIItem,POIData.Document> = HashMap()
    //하나의 마커 설정 함수
    private fun addMapPoiMarker(data: POIData.Document):MapPOIItem{
        val marker = MapPOIItem()
        with(marker){
            tag = 0
            markerType = MapPOIItem.MarkerType.BluePin // 마커 색
            showAnimationType = MapPOIItem.ShowAnimationType.SpringFromGround
            mapPoint = MapPoint.mapPointWithGeoCoord(data.y.toDouble(), data.x.toDouble()) // poi장소 좌표
            marker.isShowCalloutBalloonOnTouch = true
            itemName = data.placeName // 장소명
        }
        markerResolver[data] = marker
        markerResolver2[marker] = data
        return marker
    }

    // 전체 마커 map 표시 함수
    private fun displayPOI(data: POIData, map:MapView){
        with(map){
            removeAllPOIItems() // 기존 마커들 제거
            setMapCenterPoint(MapPoint.mapPointWithGeoCoord(festivalLat.toDouble(), festivalLgt.toDouble()), false) // map 중심점
            addPOIItem(settingFestivalMarker()) // 행사위치 핑
            for(i in 0 until  data.documents.size){
                addPOIItem(addMapPoiMarker(data.documents[i])) // 현 마커 추가
            }
        }
    }

    // 상단 주변상권정보 카테고리 선택 버튼
    private fun setCategoryBtn(){
        with(binding){
            cafeBtn.setOnClickListener {
                poiCategory = getString(R.string.poi_category_1)
                viewModel.getAllPoiFromViewModel(poiCategory, festivalLat, festivalLgt)
            }
            restaurantBtn.setOnClickListener {
                poiCategory = getString(R.string.poi_category_2)
                viewModel.getAllPoiFromViewModel(poiCategory, festivalLat, festivalLgt)
            }
            etcBtn.setOnClickListener {
                poiCategory = getString(R.string.poi_category_3)
                viewModel.getAllPoiFromViewModel(poiCategory, festivalLat, festivalLgt)
            }
        }
    }
}