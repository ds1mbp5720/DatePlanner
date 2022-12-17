package com.lee.dateplanner.timemap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lee.dateplanner.databinding.TimesheetPlanRecyclerBinding
import com.lee.dateplanner.timemap.TimetableMapActivity
import com.lee.dateplanner.timetable.timesheet.TimeSheet
import com.lee.dateplanner.timetable.time.room.Timetable
import net.daum.mf.map.api.CameraUpdateFactory
import net.daum.mf.map.api.CancelableCallback

/**
 * 개인 일정 map(TimetableMapActivity) 의 bottomsheet에 보여질 일정 adapter
 */
class TimetableMapAdapter(private val owner: TimetableMapActivity, private val timetable: Timetable): RecyclerView.Adapter<TimetableMapViewHolder>() {
    private lateinit var binding: TimesheetPlanRecyclerBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimetableMapViewHolder {
        binding = TimesheetPlanRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimetableMapViewHolder(binding)
    }
    override fun onBindViewHolder(holder: TimetableMapViewHolder, position: Int) {
        val timeSheet = timetable.timeSheetList.get(position)
        val behavior = BottomSheetBehavior.from(owner.binding.bottomScheduleList)
        with(holder.binding) {
            // view 연결
            scheduleTitle.text = timeSheet.title
            scheduleTime.text = timeSheet.time
            schedulePlace.text = timeSheet.place
            scheduleMemo.text = timeSheet.memo

            // 리스트 터치시
            root.setOnClickListener {
                val marker = owner.markerResolver[timeSheet] // 마커와 리스트 map을 통한 연결
                // 해당 위치로 지도 중심점 이동, 지도 확대
                if (marker != null) {
                    val update = CameraUpdateFactory.newMapPoint(marker.mapPoint, 2F)
                    with(owner.binding) {
                        scheduleMap.animateCamera(update, object : CancelableCallback {
                            override fun onFinish() {
                                owner.binding.scheduleMap.selectPOIItem(marker, true) //마커 선택
                                scheduleMap.refreshMapTiles()
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

    override fun getItemCount(): Int{
        return timetable.timeSheetList.size
    }

    // 클릭 이벤트 설정 함수
    private fun listenerSetup(timeSheet: TimeSheet){
        val behavior = BottomSheetBehavior.from(owner.binding.bottomScheduleList)
    }

}