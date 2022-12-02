package com.lee.dateplanner.timetable

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.lee.dateplanner.common.*
import com.lee.dateplanner.databinding.InputScheduleLayoutBinding
import com.lee.dateplanner.timetable.onetime.TimeSheet
import com.lee.dateplanner.timetable.time.room.Timetable
import java.util.*
import kotlin.collections.ArrayList

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

        /**
         * 경우의 수 함수로 구하기 inputTypeSignal 로 구분
         * 1. 일정 추가: add  2.기존 계획 수정: change  2. 행사: festival 혹은 poi 정보: poi  추가
         */
        // 기존 계획에 일정 추가시
        setInputType(intent.getStringExtra("inputTypeSignal"))
    }
    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
    // 입력 혹은 수정, 추가 경우에 따른 입력 기능 분류 함수
    private fun setInputType(type: String?){
        when(type){
            "add" -> { // 기존 일자에 일정 추가, TimetableRecyclerAdapter
                val id = intent.getIntExtra("id",0) // 터치한 계획표 id 값 받기
                viewModel.findTimetable(id)
                viewModel.getSearchResults().observe(this){ timetable ->
                    timetable?.let {
                        setListener(id,timetable[0])
                    }
                }
            }"change" -> { // 기존 입력된 일자에서 변경시

            }"festival" -> { // 행사를 일정에 추가시, FestivalRecyclerAdapter

            }"poi" -> { // poi 정보를 일정에 추가시

            }else -> {
                Log.e(TAG,"Wrong Type")
            }
        }
    }
    // 버튼 설정
    private fun setListener(id: Int, timeTable : Timetable){
        // 시간 버튼 클릭
        binding.inputTimeBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                val time = timeStringFormat(hourOfDay,minute)
                binding.inputTimeBtn.text = time
            }
            TimePickerDialog(this,timeSetListener,cal.get(Calendar.HOUR_OF_DAY), cal.get(
                Calendar.MINUTE), android.text.format.DateFormat.is24HourFormat(this)).show()

        }
        //등록 선택시
        binding.insertBtn.setOnClickListener {
            val timeSheetList = timeTable?.timeSheetList as ArrayList<TimeSheet>?
            //입력값들 일정표에 저장
            with(binding){
                val title = inputTitle.text.toString()
                val time = inputTimeBtn.text.toString()
                val place = inputLocation.text.toString()
                val memo = inputMemo.text.toString()
                timeSheetList!!.add(TimeSheet(title,time,place,memo,"","")) // 저장용 timesheet
            }

            if(timeTable != null){
                timeTable.timeSheetList = timeSheetList // 새로 추가된 list 로 교체
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