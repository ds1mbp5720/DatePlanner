package com.lee.dateplanner.timemap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.lee.dateplanner.databinding.MyScheduleMapActivityLayoutBinding
import com.lee.dateplanner.map.POIBallonClickListner
import com.lee.dateplanner.map.adpter.POIWindowAdapter
import com.lee.dateplanner.timemap.adapter.TimetableMapAdapter
import com.lee.dateplanner.timetable.TimetableViewModel
import com.lee.dateplanner.timetable.timesheet.TimeSheet
import com.lee.dateplanner.timetable.time.room.Timetable
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint

/**
 * 시간 계획표 마커 표시 지도 activity
 */
class TimetableMapActivity:AppCompatActivity() {
    lateinit var binding: MyScheduleMapActivityLayoutBinding
    private lateinit var viewModel: TimetableViewModel
    private val poiBallonListner = POIBallonClickListner(this) // info window 터치 객체

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MyScheduleMapActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(TimetableViewModel::class.java)

        val id = intent.getIntExtra("id",0)
        mapSetting()

        viewModel.findTimetable(id)
        viewModel.getSearchResults().observe(this){ timetable ->
            timetable?.let {
                with(binding.scheduleInfoRecycler){
                    run{
                        // bottomsheet에 보여질 list 에 대한 adpater 연결
                        val scheduleAdapter = TimetableMapAdapter(this@TimetableMapActivity,timetable[0])
                        adapter = scheduleAdapter
                        scheduleAdapter
                    }
                }
            }
            displayPOI(timetable[0])
        }
    }

    //카카오 지도 설정
    private fun mapSetting() {
        with(binding.scheduleMap) {
            setDaumMapApiKey(com.lee.dateplanner.R.string.kakao_map_key.toString())
            mapType = net.daum.mf.map.api.MapView.MapType.Standard
            setZoomLevel(3, true)
            zoomIn(true)
            zoomOut(true)
            setPOIItemEventListener(poiBallonListner)
            setCalloutBalloonAdapter(POIWindowAdapter(this.context))
        }
    }
    //recyclerView 에서 터치시 해당 maker 로 이동하기 위한 map
    var markerResolver: MutableMap<TimeSheet,MapPOIItem> = HashMap()
    private fun addMapPoiMarker(data: TimeSheet): MapPOIItem {
        val marker = MapPOIItem()
        with(marker){
            tag = 0
            markerType = MapPOIItem.MarkerType.BluePin // 마커 색
            mapPoint = MapPoint.mapPointWithGeoCoord(data.lat.toDouble(), data.lgt.toDouble()) // poi장소 좌표
            itemName = data.title // 일정 이름
        }
        markerResolver[data] = marker // 마커와 list 연결
        return marker
    }

    // 전체 마커 map 표시 함수
    private fun displayPOI(data: Timetable){
        with(binding.scheduleMap){
            removeAllPOIItems() // 기존 마커들 제거
            if(data.timeSheetList != null){
                for(i in 0 until (data.timeSheetList!!.size)){
                    // 위치 좌표가 없는 일정의 경우 마커 생성 생략
                    if(data.timeSheetList!![i].lat != "" && data.timeSheetList!![i].lat != "" ){
                        addPOIItem(addMapPoiMarker(data.timeSheetList!![i])) // 현 마커 추가
                    }
                }
                //일정 map 실행시 최초 지도 중심점을 첫번째 일정의 위치로
                if(data.timeSheetList!![0].lat != "" && data.timeSheetList!![0].lat != "")
                {
                    setMapCenterPoint(net.daum.mf.map.api.MapPoint.mapPointWithGeoCoord(data.timeSheetList!![0].lat.toDouble(),data.timeSheetList!![0].lgt.toDouble()), false)
                }
            }
        }
    }
    // 종료시
    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}