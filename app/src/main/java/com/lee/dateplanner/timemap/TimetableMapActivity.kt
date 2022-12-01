package com.lee.dateplanner.timemap

import android.app.Activity
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.lee.dateplanner.R
import com.lee.dateplanner.databinding.MyScheduleMapActivityLayoutBinding
import com.lee.dateplanner.map.POIBallonClickListner
import com.lee.dateplanner.map.adpter.POIWindowAdapter
import com.lee.dateplanner.timemap.adapter.TimetableMapAdapter
import com.lee.dateplanner.timetable.TimetableViewModel
import com.lee.dateplanner.timetable.onetime.TimeSheet
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
    var markerResolver: MutableMap<TimeSheet,MapPOIItem> = HashMap()
    private fun addMapPoiMarker(data: TimeSheet): MapPOIItem {
        val marker = MapPOIItem()
        with(marker){
            tag = 0
            markerType = MapPOIItem.MarkerType.BluePin // 마커 색
            mapPoint = MapPoint.mapPointWithGeoCoord(data.lat.toDouble(), data.lgt.toDouble()) // poi장소 좌표
            itemName = data.title // 일정 이름
        }
        markerResolver[data] = marker
        return marker
    }

    // 전체 마커 map 표시 함수
    private fun displayPOI(data: Timetable){
        with(binding.scheduleMap){
            removeAllPOIItems() // 기존 마커들 제거
            if(data.timeSheetList != null){
                for(i in 0 until (data.timeSheetList!!.size)){
                    addPOIItem(addMapPoiMarker(data.timeSheetList!![i])) // 현 마커 추가
                }
                setMapCenterPoint(net.daum.mf.map.api.MapPoint.mapPointWithGeoCoord(data.timeSheetList!![0].lat.toDouble(),data.timeSheetList!![0].lgt.toDouble()), false) // map 중심점
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}