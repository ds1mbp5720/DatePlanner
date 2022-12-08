package com.lee.dateplanner.timetable.time.adapter

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.lee.dateplanner.R
import com.lee.dateplanner.common.dateStringFormat
import com.lee.dateplanner.databinding.TimetableRecyclerLayoutBinding
import com.lee.dateplanner.timemap.TimetableMapActivity
import com.lee.dateplanner.timetable.insert.InsertTimeSheetActivity
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
        return TimetableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimetableViewHolder, position: Int) {
        with(holder.binding){
            timetableList.let {
                // view 설정
                timeSheetAdapter = TimeSheetAdapter(it!![position].id, it!![position].timeSheetList!!,fragment,it[position],viewModel)  // 일일 계획
                dayTimeTable.adapter = timeSheetAdapter
                tableDateBtn.text = it!![position].date.toString()  // 일정날짜
            }
            //날짜 선택 버튼
            tableDateBtn.setOnClickListener {
                val cal = Calendar.getInstance()
                val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    val date = dateStringFormat(month,dayOfMonth) // string 양식에 맞춰서 저장
                    tableDateBtn.text = date // 날짜 버튼 text 선택한 날짜로 변경
                    // roomDB 해당 일정 date 최신화
                    viewModel.updateDate(tableDateBtn.text.toString(), timetableList!![position]?.id as Int)
                }
                //달력 띄우기
                fragment.context?.let { it1 -> DatePickerDialog(it1,dateSetListener,cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
                }
            }

            // 시간계획 추가 버튼
            addTimesheetBtn.setOnClickListener {
                val intent = Intent(holder.itemView.context, InsertTimeSheetActivity::class.java)
                intent.putExtra("input_signal","add") // 입력신호 추가
                intent.putExtra("id",(timetableList!![position].id)) // 전달할 timesheet
                startActivity(holder.itemView.context, intent, null) // 추가 계획 입력 페이지 이동
            }

            // 내 시간계획 지도 이동 버튼
            myMapBtn.setOnClickListener {
                val intent = Intent(holder.itemView.context, TimetableMapActivity::class.java)
                intent.putExtra("id",(timetableList!![position].id)) // 전달할 timesheet
                startActivity(holder.itemView.context, intent, null) // 일정 지도 페이지 이동
            }

            // 해당 일일 일정 삭제
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