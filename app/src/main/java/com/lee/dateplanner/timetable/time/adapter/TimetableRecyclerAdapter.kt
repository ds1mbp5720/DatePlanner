package com.lee.dateplanner.timetable.time.adapter

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.lee.dateplanner.databinding.TimetableRecyclerLayoutBinding
import com.lee.dateplanner.timemap.TimetableMapActivity
import com.lee.dateplanner.timetable.InsertTimeTableActivity
import com.lee.dateplanner.timetable.TimetableViewModel
import com.lee.dateplanner.timetable.onetime.adapter.TimeSheetAdapter
import com.lee.dateplanner.timetable.time.room.Timetable

class TimetableRecyclerAdapter(private val viewModel: TimetableViewModel, private val timetableItemLayout: Int): RecyclerView.Adapter<TimetableViewHolder>() {
    private lateinit var binding: TimetableRecyclerLayoutBinding
    private var timetableList: List<Timetable>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimetableViewHolder {
        binding = TimetableRecyclerLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TimetableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimetableViewHolder, position: Int) {
        with(holder.binding){
            timetableList.let {
                // view 설정
                dayTimeTable.adapter = TimeSheetAdapter(it!![position].id,it!![position].timeSheetList!!)  // 일일 계획
                Log.e(TAG,"${it!![position].timeSheetList!!}")
                tableDate.text = it!![position].date.toString()  // 날짜
            }

            // 시간계획 추가 버튼
            addTimesheetBtn.setOnClickListener {
                val intent = Intent(holder.itemView.context, InsertTimeTableActivity::class.java)
                intent.putExtra("Time",(timetableList!![position])) // 전달할 timesheet
                intent.putExtra("id",(timetableList!![position].id)) // 전달할 timesheet
                startActivity(holder.itemView.context, intent, null)
            }

            // 내 시간계획 지도 이동 버튼
            myMapBtn.setOnClickListener {
                val intent = Intent(holder.itemView.context, TimetableMapActivity::class.java)
                intent.putExtra("id",(timetableList!![position].id)) // 전달할 timesheet
                startActivity(holder.itemView.context, intent, null)
            }
            deleteTimeBtn.setOnClickListener {
                viewModel.deleteTimetable(timetableList!![position].id)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTimetableList(timetable: List<Timetable>){
        timetableList = timetable
        notifyDataSetChanged()
    }

    override fun getItemCount() = if(timetableList == null) 0 else timetableList!!.size
}