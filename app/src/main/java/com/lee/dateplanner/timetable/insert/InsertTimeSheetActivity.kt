package com.lee.dateplanner.timetable.insert

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.lee.dateplanner.R
import com.lee.dateplanner.common.*
import com.lee.dateplanner.databinding.InputScheduleLayoutBinding
import com.lee.dateplanner.timetable.TimetableViewModel
import com.lee.dateplanner.timetable.timesheet.TimeSheet
import com.lee.dateplanner.timetable.time.room.Timetable
import java.util.*
import kotlin.collections.ArrayList

/**
 * 시간계획 입력 activity
 */
class InsertTimeSheetActivity: AppCompatActivity() {
    private lateinit var binding: InputScheduleLayoutBinding
    private lateinit var viewModel: TimetableViewModel

    // 입력받을 정보들 저장 목적 변수
    private var type: String? = null
    private var id: Int = 0
    private lateinit var title: String
    private lateinit var time: String
    private lateinit var place: String
    private lateinit var memo: String
    private var position: Int = 0 // 수정시 해당 일정의 위치 정보


    @SuppressLint("Deprecated")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = InputScheduleLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(TimetableViewModel::class.java)

        type = intent.getStringExtra("input_signal")
        setInputType()
    }

    // 입력 혹은 수정, 추가 경우에 따른 입력 기능 분류 함수
    private fun setInputType(){
        when(type){
            "add" -> { // 기존 일자에 일정 추가, TimetableRecyclerAdapter
                typeNewAdd()
            }"edit" -> { // 기존 입력된 일자에서 변경시 , TimeSheetAdapter
                typeEdit()
            }"festival" -> { // 행사를 일정에 추가시, FestivalRecyclerAdapter
                typeFestival()
            }"poi" -> { // poi 정보를 일정에 추가시
                typePoi()
            }else -> {
                Log.e(TAG,"Wrong Type")
            }
        }
    }
    // 옵저버 설정 , 내부에 리스너 삽입
    private fun setObserve(){
        viewModel.findTimetable(id)
        viewModel.getSearchResults().observe(this){ timetable ->
            timetable?.let {
                Log.e(TAG,"$timetable")
                setListener(timetable[0])
            }
        }
    }
    // 버튼 설정
    private fun setListener(timeTable : Timetable){
        // 시간 버튼 클릭
        binding.inputTimeBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                val time = timeStringFormat(hourOfDay,minute)
                binding.inputTimeBtn.text = time
            }
            TimePickerDialog(this,timeSetListener,cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), android.text.format.DateFormat.is24HourFormat(this)).show()
        }
        //등록 선택시
        binding.insertBtn.setOnClickListener {
            when(type){
                "add" -> setBtnTypeAdd(timeTable)
                "edit"-> setBtnTypeEdit(timeTable)
                "festival"-> setBtnTypeFestival(timeTable)
                "poi"-> setBtnTypePoi(timeTable)
                }
            finish()
        }

        //뒤로가기 선택시
        binding.inputBackBtn.setOnClickListener {
            finish()
        }
    }
    // 신규 입력
    private fun typeNewAdd(){
        id = intent.getIntExtra("id",0) // 터치한 계획표 id 값 받기
        setObserve()
    }
    // 신규입력 추가 버튼 이벤트
    private fun setBtnTypeAdd(timeTable : Timetable){
        // 기존 리스트 가져오기
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
            timeTable.timeSheetList?.let { it1 -> viewModel.updateTimetable(it1,id) }
        }
    }

    // 기존 정보 수정
    private fun typeEdit(){
        // 전달받은 값들 저장
        id = intent.getIntExtra("id",0)
        title = intent.getStringExtra("title").toString()
        time = intent.getStringExtra("time").toString()
        place = intent.getStringExtra("place").toString()
        memo = intent.getStringExtra("memo").toString()
        position = intent.getIntExtra("position",0)

        //입력받는 view 에 적용
        with(binding){
            inputTitle.setText(title)
            inputLocation.setText(place)
            inputMemo.setText(memo)
            inputTimeBtn.setText(time) // timepicker 초기 설정
        }
        setObserve()
    }
    // 수정시 버튼 이벤트
    private fun setBtnTypeEdit(timeTable : Timetable){
        //기존 리스트 가져오기
        val timeSheetList = timeTable?.timeSheetList as ArrayList<TimeSheet>?
        //입력값들 일정표에 저장
        with(binding){
            title = inputTitle.text.toString()
            time = inputTimeBtn.text.toString()
            place = inputLocation.text.toString()
            memo = inputMemo.text.toString()
            timeSheetList?.set(position, TimeSheet(title,time,place,memo,"",""))
        }
        if(timeTable != null){
            timeTable.timeSheetList = timeSheetList // 새로 추가된 list 로 교체
            Log.e(TAG,"변화 확인 ${timeTable.timeSheetList}")
            //추가한 timesheet 업데이트
            timeTable.timeSheetList?.let { it1 -> viewModel.updateTimetable(it1,id) }
        }
    }
    // 행사 정보
    private fun typeFestival(){
        val title = intent.getStringExtra("festivalTitle")
        val place = intent.getStringExtra("festivalPlace")

        // 입력받는 view에 적용
        with(binding){
            inputTitle.setText(title)
            inputLocation.setText(place)
        }
    }
    private fun setBtnTypeFestival(timeTable : Timetable){}
    // 주변 상점 정보
    private fun typePoi(){

    }
    private fun setBtnTypePoi(timeTable : Timetable){}

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}