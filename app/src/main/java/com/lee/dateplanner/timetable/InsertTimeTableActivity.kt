package com.lee.dateplanner.timetable

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.lee.dateplanner.databinding.InputScheduleLayoutBinding
import com.lee.dateplanner.timetable.onetime.TimeSheet
import com.lee.dateplanner.timetable.time.room.Timetable

/**
 * 시간계획 입력 창
 */
class InsertTimeTableActivity: AppCompatActivity() {
    private lateinit var binding: InputScheduleLayoutBinding
    private lateinit var viewModel:TimetableViewModel

    @SuppressLint("Deprecated")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = InputScheduleLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(TimetableViewModel::class.java)

        val id = intent.getIntExtra("id",0) // 터치한 계획표 id 값 받기
        viewModel.findTimetable(id)
        viewModel.getSearchResults().observe(this){ timetable ->
            timetable?.let {
                setListner(id,timetable[0])
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }

    private fun setListner(id: Int, timeTable : Timetable){
        //등록 선택시
        binding.insertBtn.setOnClickListener {
            val timeSheetList = timeTable?.timeSheetList as ArrayList<TimeSheet>?
            //입력값들 일정표에 저장
            with(binding){
                val title = inputTitle.text.toString()
                val time = inputTime.text.toString()
                val place = inputLocation.text.toString()
                val memo = inputMemo.text.toString()
                timeSheetList!!.add(TimeSheet(title,time,place,memo,"","")) // 저장용 timesheet
            }

            if(timeTable != null){
                timeTable.timeSheetList = timeSheetList
                //추가한 timesheet 업데이트
                timeTable.timeSheetList?.let { it1 -> viewModel.updateTiemtable(it1,id) }
            }
            finish()
        }
        //뒤로가기 선택시
        binding.inputBackBtn.setOnClickListener {
            finish()
        }
    }
}