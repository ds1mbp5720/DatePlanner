package com.lee.dateplanner.timetable.time.adapter

import android.app.DatePickerDialog
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.view.clicks
import com.lee.dateplanner.R
import com.lee.dateplanner.common.copyClipBoard
import com.lee.dateplanner.common.copyToClipboard
import com.lee.dateplanner.common.dateStringFormat
import com.lee.dateplanner.common.makeDatePickerDialog
import com.lee.dateplanner.databinding.TimetableRecyclerLayoutBinding
import com.lee.dateplanner.dialog.MessageDialog
import com.lee.dateplanner.timemap.TimetableMapActivity
import com.lee.dateplanner.timetable.TimeTableFragment
import com.lee.dateplanner.timetable.TimetableViewModel
import com.lee.dateplanner.timetable.insert.InsertTimeSheetActivity
import com.lee.dateplanner.timetable.time.room.Timetable
import com.lee.dateplanner.timetable.timesheet.adapter.TimeSheetAdapter

class TimetableViewHolder(val binding: TimetableRecyclerLayoutBinding, private val fragment: TimeTableFragment,private val viewModel: TimetableViewModel): RecyclerView.ViewHolder(binding.root){
    fun setView(timetableList: List<Timetable>?, position: Int, adapter: TimeSheetAdapter?) = with(binding){
        dayTimeTable.adapter = adapter
        tableDateBtn.text = timetableList!![position].date  // 일정날짜
    }
    fun setListener(timetableList: List<Timetable>?,position: Int){
        with(binding)
        {
            //날짜 선택 버튼
            tableDateBtn.clicks().subscribe {
                val dateSetListener = DatePickerDialog.OnDateSetListener { _, _, month, dayOfMonth ->
                    tableDateBtn.text = dateStringFormat(month,dayOfMonth) // 날짜 버튼 text 선택한 날짜로 변경
                    // roomDB 해당 일정 date 최신화
                    viewModel.updateDate(tableDateBtn.text.toString(), timetableList!![position].id)
                }
                //달력 띄우기
                fragment.context?.let { it1 -> makeDatePickerDialog(it1,dateSetListener,"날짜 수정") }
            }

            // 시간계획 추가 버튼
            addTimesheetBtn.clicks().subscribe {
                val intent = Intent(itemView.context, InsertTimeSheetActivity::class.java)
                intent.putExtra("input_signal","add") // 입력신호 추가
                intent.putExtra("id",(timetableList!![position].id)) // 전달할 timesheet
                ContextCompat.startActivity(itemView.context, intent, null) // 추가 계획 입력 페이지 이동
            }

            // 내 시간계획 지도 이동 버튼
            myMapBtn.clicks().subscribe {
                val intent = Intent(itemView.context, TimetableMapActivity::class.java)
                intent.putExtra("id",(timetableList!![position].id)) // 전달할 timesheet
                ContextCompat.startActivity(itemView.context, intent, null) // 일정 지도 페이지 이동
            }

            // 해당 일일 일정 삭제
            deleteTimeBtn.clicks().subscribe{
                MessageDialog(this.root.context.getString(R.string.dialogMessageToDelete),this.root.context.getString(R.string.check),this.root.context.getString(R.string.cancel)).onRightBtn{
                    if(timetableList?.isNotEmpty() == true)
                        fragment.viewModel.deleteTimetable(timetableList[position].id)
                }.show(fragment.childFragmentManager,"")
            }
            mbCopyBtn.clicks().subscribe{
                fragment.context?.copyToClipboard( timetableList!![position].copyClipBoard()) // 해당 일정 클립보드에 복사 함수
            }
        }

    }
}