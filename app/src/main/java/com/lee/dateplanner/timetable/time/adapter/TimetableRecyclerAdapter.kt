package com.lee.dateplanner.timetable.time.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lee.dateplanner.databinding.TimetableRecyclerLayoutBinding
import com.lee.dateplanner.timetable.TimeTableFragment
import com.lee.dateplanner.timetable.TimetableViewModel
import com.lee.dateplanner.timetable.timesheet.adapter.TimeSheetAdapter
import com.lee.dateplanner.timetable.time.room.Timetable
import java.util.*

class TimetableRecyclerAdapter(private val viewModel: TimetableViewModel, private val fragment: TimeTableFragment): RecyclerView.Adapter<TimetableViewHolder>() {
    private lateinit var binding: TimetableRecyclerLayoutBinding
    private var timetableList: List<Timetable>? = null
    var timeSheetAdapter: TimeSheetAdapter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimetableViewHolder {
        binding = TimetableRecyclerLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TimetableViewHolder(binding,fragment,viewModel)
    }

    override fun onBindViewHolder(holder: TimetableViewHolder, position: Int) {
        timeSheetAdapter = TimeSheetAdapter(timetableList!![position].id, timetableList!![position].timeSheetList,fragment,timetableList!![position],viewModel)  // 일일 계획
        holder.setView(timetableList, position, timeSheetAdapter)
        holder.setListener(timetableList, position)
    }

    fun deleteTimetable(select: Boolean, timeTable: Timetable){
        if(select){
            viewModel.deleteTimetable(timeTable.id)
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setTimetableList(timetable: MutableList<Timetable>){
        timetableList = timetable
        notifyDataSetChanged()
    }

    override fun getItemCount() = if(timetableList == null) 0 else timetableList!!.size
}