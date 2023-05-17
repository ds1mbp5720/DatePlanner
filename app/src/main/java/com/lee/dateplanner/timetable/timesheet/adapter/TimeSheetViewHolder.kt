package com.lee.dateplanner.timetable.timesheet.adapter

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.view.clicks
import com.lee.dateplanner.R
import com.lee.dateplanner.databinding.TimesheetPlanRecyclerBinding
import com.lee.dateplanner.dialog.MessageDialog
import com.lee.dateplanner.timetable.TimeTableFragment
import com.lee.dateplanner.timetable.insert.InsertTimeSheetActivity
import com.lee.dateplanner.timetable.time.room.Timetable
import com.lee.dateplanner.timetable.timesheet.TimeSheet

class TimeSheetViewHolder(val binding: TimesheetPlanRecyclerBinding,private val fragment: TimeTableFragment): RecyclerView.ViewHolder(binding.root){
    fun setView(timeSheet: TimeSheet) = with(binding){
        // 기존 정보들  입력 받은 정보들
        scheduleTitle.text = timeSheet.title
        scheduleTime.text = timeSheet.time
        schedulePlace.text = timeSheet.place
        scheduleMemo.text = timeSheet.memo
    }
    fun setListener(timeTable: Timetable, timeSheet: TimeSheet, id: Int, position: Int){
        with(binding){
            //수정 버튼 클릭시
            reviseBtn.clicks().subscribe {
                val intent = Intent(itemView.context, InsertTimeSheetActivity::class.java)
                intent.putExtra("input_signal","edit") // 입력신호 수정
                intent.putExtra("title",timeSheet.title)
                intent.putExtra("time",timeSheet.time)
                intent.putExtra("place",timeSheet.place)
                intent.putExtra("memo",timeSheet.memo)
                intent.putExtra("latitude",timeSheet.lat)
                intent.putExtra("longitude",timeSheet.lgt)
                intent.putExtra("position",position) // 선택한 위치
                intent.putExtra("id",id) // 전달할 timesheet
                // id 값 받아서 넘겨주기
                ContextCompat.startActivity(itemView.context, intent, null)
            }
            //삭제버튼 클릭시
            deleteTimesheetBtn.clicks().subscribe {
                MessageDialog(this.root.context.getString(R.string.dialogMessageToDelete),this.root.context.getString(R.string.check),this.root.context.getString(R.string.cancel)).onRightBtn{
                    timeTable.timeSheetList.removeAt(position)
                    timeTable.timeSheetList.let { it1 ->
                        fragment.viewModel.updateTimetable(it1,timeTable.id)
                    }
                }.show(fragment.childFragmentManager,"")
            }
        }

    }
}