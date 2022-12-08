package com.lee.dateplanner.timetable.insert

import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.lee.dateplanner.common.*
import com.lee.dateplanner.databinding.InputScheduleLayoutBinding
import com.lee.dateplanner.map.adpter.POIWindowAdapter
import com.lee.dateplanner.timetable.TimetableViewModel
import com.lee.dateplanner.timetable.insert.dialog.SelectTimeTableDialog
import com.lee.dateplanner.timetable.timesheet.TimeSheet
import com.lee.dateplanner.timetable.time.room.Timetable
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import java.util.*
import kotlin.collections.ArrayList

/**
 * 시간계획 입력 activity
 */
class InsertTimeSheetActivity: AppCompatActivity() {
    private lateinit var binding: InputScheduleLayoutBinding
    private lateinit var viewModel: TimetableViewModel
    // 입력받을 정보들 저장 목적 변수
    private var type: String? = null
    private var id: Int = 0
    private lateinit var title: String
    private lateinit var time: String
    private lateinit var place: String
    private lateinit var memo: String
    private var latitude = "37.5143225723"
    private var longitude = "127.062831022"
    private var position: Int = 0 // 수정시 해당 일정의 위치 정보



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = InputScheduleLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(TimetableViewModel::class.java)

        type = intent.getStringExtra("input_signal")
        setInputType()
        mapSetting()
    }

    // 입력 혹은 수정, 추가 경우에 따른 입력 기능 분류 함수
    private fun setInputType(){
        when(type){
            "add" -> typeNewAdd()
            "edit" -> typeEdit()
            "apiInput" -> typeAPI()
            else -> Log.e(TAG,"Wrong Type")
        }
    }
    // 옵저버 설정 , 내부에 리스너 삽입
    private fun setObserve(){
        viewModel.findTimetable(id)
        viewModel.getSearchResults().observe(this@InsertTimeSheetActivity){ timetable ->
            timetable?.let {
                setListener(timetable[0])
            }
        }
    }
    // 버튼 설정
    private fun setListener(timeTable : Timetable){
        // 시간 버튼 클릭
        binding.inputTimeBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                val time = timeStringFormat(hourOfDay,minute)
                binding.inputTimeBtn.text = time
            }
            TimePickerDialog(this,timeSetListener,cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), android.text.format.DateFormat.is24HourFormat(this)).show()
        }
        //등록 선택시
        binding.insertBtn.setOnClickListener {
            when(type){
                "add" -> setBtnTypeAdd(timeTable)
                "edit"-> setBtnTypeEdit(timeTable)
                "apiInput"-> setBtnTypeAPI(timeTable,id)
                }
            finish()
        }
        //뒤로가기 선택시
        binding.inputBackBtn.setOnClickListener {
            finish()
        }
    }
    //카카오 지도 설정
    private fun mapSetting(){
        with(binding.insertMap) {
            mapType = net.daum.mf.map.api.MapView.MapType.Standard
            setZoomLevel(3, true)
            zoomIn(true)
            zoomOut(true)
            val selectWindow = POIWindowAdapter(this.context)
            setCalloutBalloonAdapter(selectWindow)
            if(latitude != "" && longitude != ""){
                setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude.toDouble(), longitude.toDouble()), false)
                addPOIItem(settingScheduleMarker()) // 행사위치 핑
            }
        }
    }
    private fun settingScheduleMarker(): MapPOIItem {
        val marker = MapPOIItem()
        with(marker){
            tag = 0
            markerType = MapPOIItem.MarkerType.BluePin // 마커 색
            showAnimationType = MapPOIItem.ShowAnimationType.NoAnimation
            mapPoint = MapPoint.mapPointWithGeoCoord(latitude.toDouble(), longitude.toDouble()) // 일정 장소 좌표
            itemName = "입력할 일정 장소" // 장소명
        }
        return marker
    }

    // 신규 입력
    private fun typeNewAdd(){
        id = intent.getIntExtra("id",0) // 터치한 계획표 id 값 받기
        setObserve()
    }
    // 신규입력 추가 버튼 이벤트
    private fun setBtnTypeAdd(timeTable : Timetable){
        // 기존 리스트 가져오기
        val timeSheetList = timeTable.timeSheetList as ArrayList<TimeSheet>?
        //입력값들 일정표에 저장
        with(binding){
            title = inputTitle.text.toString()
            time = inputTimeBtn.text.toString()
            place = inputLocation.text.toString()
            memo = inputMemo.text.toString()
            // 마커 위치로 값 받아오기
            //latitude
            //longitude
            timeSheetList!!.add(TimeSheet(title,time,place,memo,"","")) // 저장용 timesheet
        }
        if(timeTable != null){
            timeTable.timeSheetList = timeSheetList // 새로 추가된 list 로 교체
            //추가한 timesheet 업데이트
            timeTable.timeSheetList?.let { it1 -> viewModel.updateTimetable(it1,id) }
        }
    }

    // 기존 정보 수정
    private fun typeEdit(){
        // 전달받은 값들 저장
        id = intent.getIntExtra("id",0)
        position = intent.getIntExtra("position",0)
        title = intent.getStringExtra("title").toString()
        time = intent.getStringExtra("time").toString()
        place = intent.getStringExtra("place").toString()
        memo = intent.getStringExtra("memo").toString()
        latitude = intent.getStringExtra("latitude").toString()
        longitude = intent.getStringExtra("longitude").toString()

        //입력받는 view 에 적용
        with(binding){
            inputTitle.setText(title)
            inputLocation.setText(place)
            inputMemo.setText(memo)
            inputTimeBtn.text = time // timepicker 초기 설정
        }
        setObserve()
    }
    // 수정시 버튼 이벤트
    private fun setBtnTypeEdit(timeTable : Timetable){
        //기존 리스트 가져오기
        val timeSheetList = timeTable.timeSheetList as ArrayList<TimeSheet>?
        //입력값들 일정표에 저장
        with(binding){
            title = inputTitle.text.toString()
            time = inputTimeBtn.text.toString()
            place = inputLocation.text.toString()
            memo = inputMemo.text.toString()
            // 마커위치로 위치 변경 기능 추가
            //latitude
            //longitude
            timeSheetList?.set(position, TimeSheet(title,time,place,memo,latitude,longitude))
        }
        if(timeTable != null){
            timeTable.timeSheetList = timeSheetList // 새로 추가된 list 로 교체
            //추가한 timesheet 업데이트
            timeTable.timeSheetList?.let { it1 -> viewModel.updateTimetable(it1,id) }
        }
    }

    // 행사 정보
    private fun typeAPI(){
        title = intent.getStringExtra("title").toString()
        place = intent.getStringExtra("place").toString()
        latitude = intent.getStringExtra("latitude").toString()
        longitude = intent.getStringExtra("longitude").toString()

        // id를 dialog에서 선택하게 하기

        // 입력받는 view에 적용
        with(binding){
            inputTitle.setText(title)
            inputLocation.setText(place)
        }
        binding.inputTimeBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                val time = timeStringFormat(hourOfDay,minute)
                binding.inputTimeBtn.text = time
            }
            TimePickerDialog(this,timeSetListener,cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), android.text.format.DateFormat.is24HourFormat(this)).show()
        }
        binding.insertBtn.setOnClickListener{
            val dialog = SelectTimeTableDialog("일정을 선택하세요.",viewModel,this)
            dialog.show()
        }
    }
    fun setBtnTypeAPI(timeTable : Timetable, id: Int){

        //기존 리스트 가져오기
        val timeSheetList = timeTable?.timeSheetList as ArrayList<TimeSheet>?
        //입력값들 일정표에 저장
        with(binding){
            title = inputTitle.text.toString()
            time = inputTimeBtn.text.toString()
            place = inputLocation.text.toString()
            memo = inputMemo.text.toString()
            timeSheetList?.add(TimeSheet(title,time,place,memo, latitude,longitude))
        }
        if(timeTable != null){
            timeTable.timeSheetList = timeSheetList // 새로 추가된 list 로 교체
            //추가한 timesheet 업데이트
            timeTable.timeSheetList?.let { it1 -> viewModel.updateTimetable(it1,id) }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.root.removeAllViews()
        finish()
    }

}