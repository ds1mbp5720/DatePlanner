package com.lee.dateplanner.timemap.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lee.dateplanner.databinding.TimesheetPlanRecyclerBinding
import com.lee.dateplanner.timemap.TimetableMapActivity
import com.lee.dateplanner.timetable.onetime.TimeSheet
import com.lee.dateplanner.timetable.time.room.Timetable
import net.daum.mf.map.api.CameraUpdateFactory
import net.daum.mf.map.api.CancelableCallback

class TimetableMapAdapter(private val owner: TimetableMapActivity, private val timetable: Timetable): RecyclerView.Adapter<TimetableMapViewHolder>() {
    private lateinit var binding: TimesheetPlanRecyclerBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimetableMapViewHolder {
        binding = TimesheetPlanRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimetableMapViewHolder(binding)
    }
    override fun onBindViewHolder(holder: TimetableMapViewHolder, position: Int) {
        val timeSheet = timetable.timeSheetList?.get(position)
        val behavior = BottomSheetBehavior.from(owner.binding.bottomScheduleList)
        if(timeSheet != null) {
            with(holder.binding) {
                scheduleTitle.text = timeSheet.title
                scheduleTime.text = timeSheet.time
                schedulePlace.text = timeSheet.place

                // 리스트 터치시
                root.setOnClickListener {
                    val marker = owner.markerResolver[timeSheet]
                    // 해당 위치로 지도 중심점 이동, 지도 확대
                    if (marker != null) {
                        val update = CameraUpdateFactory.newMapPoint(marker?.mapPoint, 2F)
                        with(owner.binding) {
                            scheduleMap.animateCamera(update, object : CancelableCallback {
                                override fun onFinish() {
                                    owner.binding.scheduleMap.selectPOIItem(marker, true) //마커 선택
                                }
                                override fun onCancel() {}
                            })
                            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                }
                // bottomsheet의 일정 리스트에서 추가 버튼 클릭
                reviseBtn.setOnClickListener {

                }
            }
        }
    }

    override fun getItemCount(): Int{
        return if(timetable.timeSheetList != null) {
            timetable.timeSheetList!!.size
        }else{ 0 }
    }
    // 클릭 이벤트 설정 함수
    private fun listenerSetup(timeSheet: TimeSheet){
        val behavior = BottomSheetBehavior.from(owner.binding.bottomScheduleList)
    }

}