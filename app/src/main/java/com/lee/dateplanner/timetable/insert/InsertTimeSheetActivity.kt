package com.lee.dateplanner.timetable.insert

import android.app.Activity
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isEmpty
import com.jakewharton.rxbinding4.view.clicks
import com.lee.dateplanner.R
import com.lee.dateplanner.base.BaseActivity
import com.lee.dateplanner.common.*
import com.lee.dateplanner.databinding.InputScheduleLayoutBinding
import com.lee.dateplanner.timetable.TimetableViewModel
import com.lee.dateplanner.timetable.insert.address.AddressMapActivity
import com.lee.dateplanner.timetable.insert.dialog.SelectTimeTableDialog
import com.lee.dateplanner.timetable.timesheet.TimeSheet
import com.lee.dateplanner.timetable.time.room.Timetable
import dagger.hilt.android.AndroidEntryPoint
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

/**
 * 시간계획 입력 activity
 */
@AndroidEntryPoint
class InsertTimeSheetActivity: BaseActivity<InputScheduleLayoutBinding,TimetableViewModel>() ,MapView.POIItemEventListener{
    override val layoutId: Int = R.layout.input_schedule_layout
    override val viewModel: TimetableViewModel by viewModels()
    // 입력받을 정보들 저장 목적 변수
    private var type: String? = null
    private var id: Int = 0
    private var title: String = ""
    private var time: String = ""
    private var place: String = ""
    private var memo: String = ""
    // todo: 추후 환경설정으로 최초 좌표 설정 넣어보기
    private var latitude = "37.5143225723"
    private var longitude = "127.062831022"
    private var position: Int = 0 // 수정시 해당 일정의 list position 정보
    private lateinit var scheduleMarker:MapPOIItem
    private lateinit var mapView :MapView

    private val locationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK){
                result.data?.apply {
                    latitude = getDoubleExtra("latitude",0.0).toString()
                    longitude = getDoubleExtra("longitude",0.0).toString()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapView = MapView(this)
        dataBinding.insertMap.addView(mapView)
        type = intent.getStringExtra("input_signal")
        getAndSetData()
        setInputType()
        insertMapSetting()
        setListener()
    }
    override fun onResume() {
        super.onResume()
        if(dataBinding.insertMap.isEmpty()){
            mapView = MapView(this)
            dataBinding.insertMap.addView(mapView)
            insertMapSetting()
        }
    }
    override fun onPause() {
        super.onPause()
        dataBinding.insertMap.removeView(mapView)
    }

    // 입력 혹은 수정, 추가 경우에 따른 입력 기능 분류 함수
    private fun setInputType(){
        when(type){
            getString(R.string.add) -> setObserve()
            getString(R.string.edit) -> setObserve()
            getString(R.string.apiInput) -> {
                dataBinding.insertBtn.setOnClickListener{
                    val dialog = SelectTimeTableDialog("일정을 선택하세요.",viewModel,this)
                    dialog.show()
                }
            }
            else -> Log.e(TAG,getString(R.string.insertWrongType))
        }
    }
    // 옵저버 설정 , 내부에 리스너 삽입
    private fun setObserve(){
        viewModel.findTimetable(id)
        viewModel.getSearchResults().observe(this@InsertTimeSheetActivity){ timetable ->
            timetable?.let {
                dataBinding.insertBtn.clicks().subscribe{
                    when(type){
                        getString(R.string.add) -> setInsertData(timetable[0],id,getString(R.string.add))
                        getString(R.string.edit)-> setInsertData(timetable[0],id,getString(R.string.edit))
                        getString(R.string.apiInput)-> setBtnTypeAPI(timetable[0],id)
                    }
                    finish()
                }
            }
        }
    }
    // 공통 이벤트 처리시하는곳
    private fun setListener(){
        dataBinding.inputBackBtn.clicks().subscribe{
            finish()
        }
        dataBinding.inputTimeBtn.clicks().subscribe{
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                dataBinding.inputTimeBtn.text = timeStringFormat(hourOfDay,minute)
            }
            makeTimePickerDialog(this,timeSetListener)
        }
        dataBinding.setAddressBtn.clicks().subscribe{
            locationLauncher.launch(
                Intent(this, AddressMapActivity::class.java).apply {
                    putExtra("lgt",0.0)
                    putExtra("lng",0.0)
                }
            )
        }
        dataBinding.showMapCheckbox.setOnCheckedChangeListener { _, isChecked ->
            with(mapView){
                if(isChecked) addPOIItem(scheduleMarker)
                else removeAllPOIItems()
            }
        }
    }
    //카카오 지도 설정
    private fun insertMapSetting(){
        mapSetting(mapView,this,this@InsertTimeSheetActivity)
        with(mapView) {
            if(latitude != "" && longitude != ""){
                scheduleMarker = settingMarker(getString(R.string.insertMarkerTitle),latitude.toDouble(),longitude.toDouble(),true,MapPOIItem.MarkerType.BluePin)
                setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude.toDouble(), longitude.toDouble()), false)
                removeAllPOIItems()
                addPOIItem(scheduleMarker)
            }
        }
    }
    private fun getAndSetData(){
        id = intent.getIntExtra("id",0)
        position = intent.getIntExtra("position",0)
        title = intent.getStringExtra("title").toString()
        time = intent.getStringExtra("time").toString()
        place = intent.getStringExtra("place").toString()
        memo = intent.getStringExtra("memo").toString()
        if(type == getString(R.string.edit) || type == getString(R.string.apiInput)) {
            latitude = intent.getStringExtra("latitude").toString()
            longitude = intent.getStringExtra("longitude").toString()
        }
        with(dataBinding){
            if(title != "null") inputTitle.setText(title)
            if(place != "null") inputLocation.setText(place)
            if(memo != "null") inputMemo.setText(memo)
            if(time != "null")  inputTimeBtn.text = time // timepicker 초기 설정
        }
    }
    // adapter 통한 날짜 선택시 넘겨야하는 경우도 있어 함수로 실행
    fun setBtnTypeAPI(timeTable : Timetable, id: Int){
        setInsertData(timeTable,id,getString(R.string.apiInput))
    }
    private fun setInsertData(timeTable : Timetable, id: Int, type: String){
        with(dataBinding){
            title = inputTitle.text.toString()
            time = inputTimeBtn.text.toString()
            place = inputLocation.text.toString()
            memo = inputMemo.text.toString()
            when(type){
                getString(R.string.edit) -> timeTable.timeSheetList[position]=(TimeSheet(title,time,place,memo, latitude,longitude))
                else -> timeTable.timeSheetList.add(TimeSheet(title,time,place,memo, latitude,longitude))
            }
            timeTable.timeSheetList.sortBy { it.time }
            timeTable.timeSheetList.let { it1 -> viewModel.updateTimetable(it1,id) }
        }
    }

    // 지도 이벤트
    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {}
    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {}
    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?, p2: MapPOIItem.CalloutBalloonButtonType?){}
    // 일정 위치 조정시 drag 한 marker 좌표 획득 함수
    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
        if (p2 != null) {
            latitude = p2.mapPointGeoCoord.latitude.toString()
            longitude = p2.mapPointGeoCoord.longitude.toString()
        }
    }
}