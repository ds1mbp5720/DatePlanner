package com.lee.dateplanner.timetable.timesheet.adapter

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lee.dateplanner.R
import com.lee.dateplanner.databinding.TimesheetPlanRecyclerBinding
import com.lee.dateplanner.timetable.TimeTableFragment
import com.lee.dateplanner.timetable.TimetableViewModel
import com.lee.dateplanner.timetable.insert.InsertTimeSheetActivity
import com.lee.dateplanner.timetable.insert.dialog.SelectTimeTableDialog
import com.lee.dateplanner.timetable.time.room.Timetable
import com.lee.dateplanner.timetable.timesheet.TimeSheet
import com.lee.dateplanner.timetable.timesheet.dialog.DeleteTimeSheetDialog

class TimeSheetAdapter(private var id: Int, private var timesheetList: List<TimeSheet>,private val owner: TimeTableFragment,
                       private var timeTable: Timetable, private val viewModel: TimetableViewModel): RecyclerView.Adapter<TimeSheetViewHolder>() {
    private lateinit var binding: TimesheetPlanRecyclerBinding
    var dialog: DeleteTimeSheetDialog? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSheetViewHolder {
        binding = TimesheetPlanRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeSheetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeSheetViewHolder, position: Int) {
        val timesheet = timesheetList[position]
        with(holder.binding){
            // 기존 정보들  입력 받은 정보들
            scheduleTitle.text = timesheet.title
            scheduleTime.text = timesheet.time
            schedulePlace.text = timesheet.place

            //수정 버튼 클릭시
            reviseBtn.setOnClickListener {
                val intent = Intent(holder.itemView.context, InsertTimeSheetActivity::class.java)
                intent.putExtra("input_signal","edit") // 입력신호 수정
                intent.putExtra("title",timesheet.title)
                intent.putExtra("time",timesheet.time)
                intent.putExtra("place",timesheet.place)
                intent.putExtra("memo",timesheet.memo)
                intent.putExtra("latitude",timesheet.lat)
                intent.putExtra("longitude",timesheet.lgt)
                intent.putExtra("position",position) // 선택한 위치
                intent.putExtra("id",id) // 전달할 timesheet
                // id 값 받아서 넘겨주기
                ContextCompat.startActivity(holder.itemView.context, intent, null)
            }
            //삭제버튼 클릭시
            deleteTimesheetBtn.setOnClickListener {
                    val timeSheetList = timeTable.timeSheetList as ArrayList<TimeSheet>?
                    timeSheetList?.removeAt(position)
                    if(timeTable != null){
                        timeTable.timeSheetList = timeSheetList // 새로 추가된 list 로 교체
                        //추가한 timesheet 업데이트
                        timeTable.timeSheetList?.let { it1 ->
                            viewModel.updateTimetable(it1,id)
                        }
                    }
                //dialog = DeleteTimeSheetDialog(owner,id, position)
                //dialog!!.show()
            }
        }
    }
    fun dialogCallBack(select: Boolean, position: Int){
        if(select){

        }else{

        }
    }
    override fun getItemCount() = timesheetList.size
}