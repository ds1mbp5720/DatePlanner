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
import com.lee.dateplanner.common.mapSetting
import com.lee.dateplanner.common.settingMarker
import com.lee.dateplanner.databinding.AroundinfoMapFragmentLayoutBinding
import com.lee.dateplanner.map.adpter.POIRecyclerAdapter
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
    private val poiBalloonListener = POIEventClickListener(this) // info window 터치 객체
    private var poiCategory: String = "CE7" // category 저장 변수
    private var festivalLat: String = "37.5143225723" // 행사장 좌표
    private var festivalLgt: String = "127.062831022" // 행사장 좌표
    private lateinit var festivalMarker: MapPOIItem
    private var job : Job? = null
    var selectMarkerPOIFragment = SelectMarkerPOIFragment()
    // 정보 리스트와 maker 연동 목적 map
    var markerResolver: MutableMap<POIData.Document,MapPOIItem> = HashMap()
    var markerResolver2: MutableMap<MapPOIItem,POIData.Document> = HashMap()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AroundinfoMapFragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onPause() {
        super.onPause()
        binding.root.removeView(binding.root.findViewById(R.id.info_map))
    }
    override fun onResume() {
        super.onResume()
        festivalMarker = settingMarker(getString(R.string.festivalMarkerTitle),festivalLat.toDouble(),festivalLgt.toDouble(),false,MapPOIItem.MarkerType.RedPin)
        if(binding.root.findViewById<MapView>(R.id.info_map) == null){
            binding.root.addView(createNewMapView(),0)
        }
    }

    private fun createNewMapView(): MapView{
        val poiMap = MapView(this.activity)
        val layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        with(poiMap){
            poiMap.layoutParams = layoutParams
            id=R.id.info_map
            mapSetting(this, this@POIMapFragment.requireContext(),poiBalloonListener)
            addPOIItem(festivalMarker) // 새로 생성한 map 행사위치 핑
            observerSetup(viewModel,this)
        }
        return poiMap
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, POIViewModelFactory(
            POIRepository(POIRetrofitService.getInstance())
        ))[POIViewModel::class.java]
        getFestivalPosition()
        festivalMarker = settingMarker(getString(R.string.festivalMarkerTitle),festivalLat.toDouble(),festivalLgt.toDouble(),false,MapPOIItem.MarkerType.RedPin)
        mapSetting(binding.infoMap, this@POIMapFragment.requireContext(),poiBalloonListener)
        binding.infoMap.addPOIItem(festivalMarker) // 행사위치 핑
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
        setFragmentResultListener("positionKey"){ _, bundle ->
            festivalLat = bundle.getDouble("latitude").toString()
            festivalLgt = bundle.getDouble("longitude").toString()
            // 내부에서만 값이 실시간 반영이 되므로 retrofit 함수는 여기서 실행
            viewModel.getAllPoiFromViewModel(poiCategory, festivalLat, festivalLgt)
        }
    }
    // 전체 마커 map 표시 함수
    private fun displayPOI(data: POIData, map:MapView){
        with(map){
            removeAllPOIItems() // 기존 마커들 제거
            setMapCenterPoint(MapPoint.mapPointWithGeoCoord(festivalLat.toDouble(), festivalLgt.toDouble()), false) // map 중심점
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