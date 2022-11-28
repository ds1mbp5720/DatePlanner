package com.lee.dateplanner.timetable.time.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lee.dateplanner.databinding.TimetableRecyclerLayoutBinding
import com.lee.dateplanner.festival.WebViewFestivalActivity
import com.lee.dateplanner.timetable.InsertTimeTableActivity
import com.lee.dateplanner.timetable.onetime.adapter.TimeSheetAdapter
import com.lee.dateplanner.timetable.time.room.Timetable

class TimetableRecyclerAdapter(private val timetableItemLayout: Int): RecyclerView.Adapter<TimetableViewHolder>() {
    private lateinit var binding: TimetableRecyclerLayoutBinding
    private var timetableList: List<Timetable>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimetableViewHolder {
        binding = TimetableRecyclerLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TimetableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimetableViewHolder, position: Int) {
        with(holder.binding){
            timetableList.let {
                dayTimeTable.adapter = TimeSheetAdapter(it!![position].timeSheetList!!)
                tableDate.text = it!![position].date.toString()
            }
            // 시간계획 추가 버튼
            addTimesheetBtn.setOnClickListener {
                val intent = Intent(holder.itemView.context, InsertTimeTableActivity::class.java)
                ContextCompat.startActivity(holder.itemView.context, intent, null)
            }
            // 시간계획 지도 버튼
            myMapBtn.setOnClickListener {

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