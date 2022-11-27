package com.lee.dateplanner.map

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.lee.dateplanner.R
import com.lee.dateplanner.databinding.AroundinfoMapFragmentLayoutBinding
import com.lee.dateplanner.map.adpter.POIRecyclerAdapter
import com.lee.dateplanner.map.adpter.POIWindowAdapter
import com.lee.dateplanner.map.data.POIData
import com.lee.dateplanner.map.network.POIRetrofitService
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class AroundMapFragment:Fragment(){
    companion object{
        fun newInstance() = AroundMapFragment()
    }
    lateinit var binding: AroundinfoMapFragmentLayoutBinding
    private val poiBallonListner = POIBallonClickListner(this.context) // info window 터치 객체
    private lateinit var map: MapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AroundinfoMapFragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: POIViewModel = ViewModelProvider(this, POIViewModelFactory(
            POIRepository(POIRetrofitService.getInstance())
        )).get(POIViewModel::class.java)

        mapSetting() // 기본 kakao map 셋팅
        observerSetup(viewModel)
        viewModel.getAllPoiFromViewModel()
    }

    // 옵저버 세팅
    private fun observerSetup(viewModel: POIViewModel){
        viewModel.poiList.observe(viewLifecycleOwner){
            with(binding.poiInfoRecycler){
                run{
                    val poiAdapter = POIRecyclerAdapter(this@AroundMapFragment,it)
                    adapter = poiAdapter
                    poiAdapter
                }
            }
            displayPOI(it)
        }
        viewModel.errorMessage.observe(viewLifecycleOwner){
            Log.e(TAG,it)
        }
    }
    //카카오 지도 설정
    private fun mapSetting() {
        with(binding.infoMap) {
            setDaumMapApiKey(R.string.kakao_map_key.toString())
            mapType = MapView.MapType.Standard
            setZoomLevel(3, true)
            zoomIn(true)
            zoomOut(true)
            setPOIItemEventListener(poiBallonListner)
            setCalloutBalloonAdapter(POIWindowAdapter(this.context))
            // 지금은 임시로 좌표 지정 -> 행사장소 변수 연결로 변경 예정
            setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.5143225723, 127.062831022), false) // map 중심점
        }
    }

    var markerResolver: MutableMap<POIData.Document,MapPOIItem> = HashMap() // 정보 리스트와 maker 연동 목적
    //하나의 마커 설정 함수
    private fun addMapPoiMarker(data: POIData.Document):MapPOIItem{
        val marker = MapPOIItem()
        with(marker){
            tag = 0
            markerType = MapPOIItem.MarkerType.BluePin // 마커 색
            mapPoint = MapPoint.mapPointWithGeoCoord(data.y.toDouble(), data.x.toDouble()) // poi장소 좌표
            itemName = data.placeName // 장소명
        }
        markerResolver[data] = marker
        return marker
    }

    // 전체 마커 map 표시 함수
    private fun displayPOI(data: POIData){
        with(binding.infoMap){
            removeAllPOIItems() // 기존 마커들 제거
            for(i in 0 until  data.documents.size){
                addPOIItem(addMapPoiMarker(data.documents[i])) // 현 마커 추가
            }
        }
    }
}