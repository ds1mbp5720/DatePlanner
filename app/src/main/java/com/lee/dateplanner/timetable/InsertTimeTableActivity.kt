package com.lee.dateplanner.timetable

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.lee.dateplanner.databinding.FestivalWebviewActivityBinding
import com.lee.dateplanner.databinding.InputScheduleLayoutBinding
import com.lee.dateplanner.timetable.onetime.TimeSheet
import com.lee.dateplanner.timetable.time.room.Timetable
import kotlin.math.log

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

        val id = intent.getIntExtra("id",0)
        viewModel.findTimetable(id)
        viewModel.getSearchResults().observe(this){ timetable ->
            timetable?.let {
                Log.e(TAG,"inner ${timetable[0].timeSheetList}")
                var timeTable = timetable[0]

                setListner(id,timetable[0])

                Log.e(TAG, "${timeTable!!.timeSheetList}")
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
            //입력값들 저장
            with(binding){
                val title = inputTitle.text.toString()
                val time = inputTime.text.toString()
                val place = inputLocation.text.toString()
                val memo = inputMemo.text.toString()
                timeSheetList!!.add(TimeSheet(title,time,place,memo,"","")) // 저장용 timesheet
            }

            if(timeTable != null){
                timeTable.timeSheetList = timeSheetList
                Log.e(TAG,"결과는 ${timeTable.timeSheetList}")
                //업데이트 부분 수정해야함
                viewModel.insertTimeTable(Timetable(timeTable.timeSheetList!!,timeTable.date))
                viewModel.deleteTimetable(id)
            }
        }
        //뒤로가기 선택시
        binding.inputBackBtn.setOnClickListener {
            finish()
        }
    }
}