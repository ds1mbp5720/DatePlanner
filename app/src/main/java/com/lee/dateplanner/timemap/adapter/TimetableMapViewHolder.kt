package com.lee.dateplanner.timemap.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lee.dateplanner.databinding.TimesheetPlanRecyclerBinding
import com.lee.dateplanner.timemap.TimetableMapActivity
import com.lee.dateplanner.timetable.timesheet.TimeSheet
import net.daum.mf.map.api.CameraUpdateFactory
import net.daum.mf.map.api.CancelableCallback

class TimetableMapViewHolder(val binding: TimesheetPlanRecyclerBinding): RecyclerView.ViewHolder(binding.root){
    fun setView(timeSheet: TimeSheet) = with(binding){
        scheduleTitle.text = timeSheet.title
        scheduleTime.text = timeSheet.time
        schedulePlace.text = timeSheet.place
        scheduleMemo.text = timeSheet.memo
    }
    fun setListener(timeSheet: TimeSheet, owner: TimetableMapActivity)= with(binding){
        val behavior = BottomSheetBehavior.from(owner.dataBinding.bottomScheduleList)
        root.setOnClickListener {
            val marker = owner.markerResolver[timeSheet] // 마커와 리스트 map을 통한 연결
            // 해당 위치로 지도 중심점 이동, 지도 확대
            if (marker != null) {
                val update = CameraUpdateFactory.newMapPoint(marker.mapPoint, 2F)
                with(owner.mapView) {
                    animateCamera(update, object : CancelableCallback {
                        override fun onFinish() {
                            selectPOIItem(marker, true) //마커 선택
                            refreshMapTiles()
                        }
                        override fun onCancel() {}
                    })
                    // 리스트 구성 선택시 bottomSheet 내리기
                    behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }
        // 버튼 제거
        reviseBtn.visibility = View.GONE
        deleteTimesheetBtn.visibility = View.GONE
    }
}