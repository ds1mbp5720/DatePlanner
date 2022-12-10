package com.lee.dateplanner.timemap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.lee.dateplanner.common.mapSetting
import com.lee.dateplanner.common.settingMarker
import com.lee.dateplanner.databinding.MyScheduleMapActivityLayoutBinding
import com.lee.dateplanner.map.POIEventClickListener
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MyScheduleMapActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[TimetableViewModel::class.java]

        val id = intent.getIntExtra("id",0)
        mapSetting(binding.scheduleMap,this,POIEventClickListener())

        viewModel.findTimetable(id)
        viewModel.getSearchResults().observe(this){ timetable ->
            timetable?.let {
                with(binding.scheduleInfoRecycler){
                    run{
                        // bottomSheet 보여질 list 에 대한 adapter 연결
                        val scheduleAdapter = TimetableMapAdapter(this@TimetableMapActivity,timetable[0])
                        adapter = scheduleAdapter
                        scheduleAdapter
                    }
                }
            }
            displayPOI(timetable[0])
        }
    }
    //recyclerView 에서 터치시 해당 maker 로 이동하기 위한 map
    var markerResolver: MutableMap<TimeSheet,MapPOIItem> = HashMap()

    // 전체 마커 map 표시 함수
    private fun displayPOI(data: Timetable){
        with(binding.scheduleMap){
            removeAllPOIItems() // 기존 마커들 제거
            for(i in 0 until (data.timeSheetList.size)){
                // 위치 좌표가 없는 일정의 경우 마커 생성 생략
                if(data.timeSheetList[i].lat != "" && data.timeSheetList[i].lgt != "" ){
                    val timeSheet = data.timeSheetList[i]
                    val marker = settingMarker(timeSheet.title,timeSheet.lat.toDouble(),timeSheet.lgt.toDouble(),false,MapPOIItem.MarkerType.BluePin)
                    addPOIItem(marker) // 현 마커 추가
                    markerResolver[timeSheet] = marker
                }
            }
            for (i in 0 until data.timeSheetList.size){
                // 위치가 있는 가장 빠른 일정을 최초 지도 중심
                if(data.timeSheetList[i].lat != "" && data.timeSheetList[i].lat != "") {
                    setMapCenterPoint(MapPoint.mapPointWithGeoCoord(data.timeSheetList[i].lat.toDouble(),data.timeSheetList[i].lgt.toDouble()), false)
                    break
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