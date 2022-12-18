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
 * 개인 일정 map(TimetableMapActivity) 의 bottomSheet 보여질 일정 adapter
 */
class TimetableMapAdapter(private val owner: TimetableMapActivity, private val timetable: Timetable): RecyclerView.Adapter<TimetableMapViewHolder>() {
    private lateinit var binding: TimesheetPlanRecyclerBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimetableMapViewHolder {
        binding = TimesheetPlanRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimetableMapViewHolder(binding)
    }
    override fun onBindViewHolder(holder: TimetableMapViewHolder, position: Int) {
        val timeSheet = timetable.timeSheetList.get(position)
        holder.setView(timeSheet)
        holder.setListener(timeSheet,owner)
    }

    override fun getItemCount(): Int{
        return timetable.timeSheetList.size
    }

}