package com.lee.dateplanner.timetable.timesheet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lee.dateplanner.databinding.TimesheetPlanRecyclerBinding
import com.lee.dateplanner.timetable.TimeTableFragment
import com.lee.dateplanner.timetable.TimetableViewModel
import com.lee.dateplanner.timetable.time.room.Timetable
import com.lee.dateplanner.timetable.timesheet.TimeSheet

class TimeSheetAdapter(private var id: Int, private var timesheetList: List<TimeSheet>,private val owner: TimeTableFragment,
                       private var timeTable: Timetable, private val viewModel: TimetableViewModel): RecyclerView.Adapter<TimeSheetViewHolder>() {
    private lateinit var binding: TimesheetPlanRecyclerBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSheetViewHolder {
        binding = TimesheetPlanRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeSheetViewHolder(binding,owner)
    }

    override fun onBindViewHolder(holder: TimeSheetViewHolder, position: Int) {
        val timesheet = timesheetList[position]
        holder.setView(timesheet)
        holder.setListener(timeTable,timesheet,id, position)
    }
    fun dialogCallBack(select: Boolean, position: Int, timeTable: Timetable){
        if(select){
            timeTable.timeSheetList.removeAt(position)
            timeTable.timeSheetList.let { it1 ->
                viewModel.updateTimetable(it1,timeTable.id)
            }
        }
    }

    override fun getItemCount() = timesheetList.size
}