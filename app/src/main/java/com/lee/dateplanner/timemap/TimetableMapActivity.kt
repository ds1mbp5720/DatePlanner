package com.lee.dateplanner.timemap

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.jakewharton.rxbinding4.view.clicks
import com.lee.dateplanner.R
import com.lee.dateplanner.base.BaseActivity
import com.lee.dateplanner.common.mapSetting
import com.lee.dateplanner.common.settingMarker
import com.lee.dateplanner.common.toastMessage
import com.lee.dateplanner.databinding.MyScheduleMapActivityLayoutBinding
import com.lee.dateplanner.poimap.POIEventClickListener
import com.lee.dateplanner.timemap.adapter.TimetableMapAdapter
import com.lee.dateplanner.timetable.TimetableViewModel
import com.lee.dateplanner.timetable.time.room.Timetable
import com.lee.dateplanner.timetable.timesheet.TimeSheet
import dagger.hilt.android.AndroidEntryPoint
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

/**
 * 시간 계획표 마커 표시 지도 activity
 */
@AndroidEntryPoint
class TimetableMapActivity: BaseActivity<MyScheduleMapActivityLayoutBinding,TimetableViewModel>() {
    override val layoutId: Int = R.layout.my_schedule_map_activity_layout
    override val viewModel: TimetableViewModel by viewModels()
    private lateinit var timeTableMapAdapter : TimetableMapAdapter
    private val accessFineLocation = 1000
    lateinit var mapView :MapView
    //recyclerView 에서 터치시 해당 maker 로 이동하기 위한 map
    var markerResolver: MutableMap<TimeSheet,MapPOIItem> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.findTimetable(intent.getIntExtra("id",0))
        timeTableMapAdapter = TimetableMapAdapter(this)
        mapView = MapView(this)
        dataBinding.scheduleMap.addView(mapView)
        mapSetting(mapView,this,POIEventClickListener())
        bottomSheetDownToBackKey()
    }
    override fun onResume() {
        super.onResume()
        if(dataBinding.scheduleMap.isEmpty()){
            mapView = MapView(this)
            dataBinding.scheduleMap.addView(mapView)
            mapSetting(mapView,this,POIEventClickListener())
        }
    }
    override fun onPause() {
        super.onPause()
        dataBinding.scheduleMap.removeView(mapView)
    }

    override fun initObserve() {
        super.initObserve()
        viewModel.getSearchResults().observe(this){ timetable ->
            timetable?.let {
                with(dataBinding.scheduleInfoRecycler){
                    run{
                        // bottomSheet 보여질 list 에 대한 adapter 연결
                        timeTableMapAdapter.setTimetable(timetable[0].timeSheetList)
                        adapter = timeTableMapAdapter
                    }
                }
            }
            displayPOI(timetable[0])
        }
    }
    override fun initViews() {
        super.initViews()
        dataBinding.searchLocation.clicks().subscribe {
            if (checkLocationService()) { // GPS가 켜져있을 경우
                permissionCheck()
            } else { // GPS가 꺼져있을 경우
                toastMessage(getString(R.string.askGPS))
            }
            dataBinding.searchLocation.visibility = View.INVISIBLE
            dataBinding.stopLocation.visibility = View.VISIBLE
        }
        dataBinding.stopLocation.clicks().subscribe {
            stopTracking()
            dataBinding.searchLocation.visibility = View.VISIBLE
            dataBinding.stopLocation.visibility = View.INVISIBLE
        }
    }
    // 전체 마커 map 표시 함수
    private fun displayPOI(data: Timetable){
        with(mapView){
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
    // 위치 권한 확인
    private fun permissionCheck() {
        val preference = getPreferences(MODE_PRIVATE)
        val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 상태
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
                // 권한 거절 (다시 한 번 물어봄)
                val builder = AlertDialog.Builder(this)
                builder.setMessage(getString(R.string.checkPermissonMent))
                builder.setPositiveButton(getString(R.string.check)) { _, _ ->
                    ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), accessFineLocation)
                }
                builder.setNegativeButton(getString(R.string.cancel)) { _, _ ->
                }
                builder.show()
            } else {
                if (isFirstCheck) {
                    // 최초 권한 요청
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), accessFineLocation)
                } else {
                    // 다시 묻지 않음 클릭 (앱 정보 화면으로 이동)
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage(getString(R.string.checkSettingLocationMent))
                    builder.setPositiveButton(getString(R.string.check)) { _, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                        startActivity(intent)
                    }
                    builder.setNegativeButton(getString(R.string.cancel)) { _, _ -> }
                    builder.show()
                }
            }
        } else {
            // 권한이 있는 상태
            startTracking()
        }
    }
    // 권한 요청 후 행동
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == accessFineLocation) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 요청 후 승인됨 (추적 시작)
                toastMessage(getString(R.string.acceptLocation))
                startTracking()
            } else {
                // 권한 요청 후 거절됨 (다시 요청 or 토스트)
                toastMessage(getString(R.string.refuseLocation))
                permissionCheck()
            }
        }
    }
    // GPS 켜져있는지 확인
    private fun checkLocationService(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    // 위치추적 시작
    private fun startTracking() {
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
    }
    // 위치추적 중지
    private fun stopTracking() {
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
    }

    private fun bottomSheetDownToBackKey(){
        val behavior = BottomSheetBehavior.from(dataBinding.bottomScheduleList)
        onBackPressedDispatcher?.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }else{
                    stopTracking()
                    finish()
                }
            }
        })
    }
    // 종료시
    override fun onDestroy() {
        super.onDestroy()
        stopTracking()
        finish()
    }
}