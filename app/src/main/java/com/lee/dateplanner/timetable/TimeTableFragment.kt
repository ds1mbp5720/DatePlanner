package com.lee.dateplanner.timetable

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.lee.dateplanner.MainActivity
import com.lee.dateplanner.R
import com.lee.dateplanner.databinding.TimetablelistFragmentLayoutBinding
import com.lee.dateplanner.timetable.onetime.TimeSheet
import com.lee.dateplanner.timetable.time.adapter.TimetableRecyclerAdapter
import com.lee.dateplanner.timetable.time.room.Timetable

/**
 * 저장된 시간계획 제공 fragment
 */
class TimeTableFragment:Fragment() {
    companion object{
        fun newInstance() = TimeTableFragment()
    }
    private val viewModel: TimetableViewModel by viewModels()  // 뷰모델
    private var timetableAdapter: TimetableRecyclerAdapter? = null  // 시간계획 recyclerview adapter
    private lateinit var binding: TimetablelistFragmentLayoutBinding
    private var tableCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TimetablelistFragmentLayoutBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*val viewModel: TimetableViewModel = ViewModelProvider(this, TimeTableViewModelFactory(
            TimetableRepository(context as Application))
        ).get(TimetableViewModel::class.java)*/

        listenerSetup()
        observerSetup(viewModel)
        uiSetup()
    }

    /**
     * view의 입력, 버튼 셋팅
     */
    //@SuppressLint("SetTextI18n")
    private fun listenerSetup(){
        // 계획 추가 버튼 클릭시
        binding.addBtn.setOnClickListener {
            var timeSheetList =  mutableListOf<TimeSheet>()

            /**
             * 결과 출력 확인용 hard coding data 들
             */
            var emptyTimeSheet1 = TimeSheet("000공연","13:30","서울시 00동 00","공연","37.5143225723","127.062831022")
            var emptyTimeSheet2 = TimeSheet("000카페","16:30","서울시 00동 00","커피","37.510404219518","127.06429358258")
            var emptyTimeSheet3 = TimeSheet("000식당","17:30","서울시 00동 00","밥","37.51486885062181","127.05880695418199")
            var emptyTimeSheet4 = TimeSheet("메가박스","19:30","서울 강남구 삼성동 15","영화","37.5130779481089","127.058215118259")
            timeSheetList.add(emptyTimeSheet1)
            timeSheetList.add(emptyTimeSheet2)
            timeSheetList.add(emptyTimeSheet3)
            timeSheetList.add(emptyTimeSheet4)

            /**
             * 계획 추가 버튼을 dialog 로 하여 선택한 날짜를 바로 가져와서 변수에 넣기
             */
            var date = "00.00"
            // room db 추가
            viewModel.insertTimeTable(Timetable(tableCount,timeSheetList ,date))
            tableCount++
        }
    }

    /**
     * 옵저버 셋팅
     */
    @SuppressLint("SetTextI18n")
    private fun observerSetup(viewModel: TimetableViewModel){
        viewModel.getAllTimetables()?.observe(viewLifecycleOwner){ timetable ->
            timetable?.let {
                timetableAdapter?.setTimetableList(it)
            }
        }
    }

    /**
     * ui 셋팅 함수
     */
    private fun uiSetup(){
        with(binding.allTimeTable){
            timetableAdapter = TimetableRecyclerAdapter(viewModel,this@TimeTableFragment)
            adapter = timetableAdapter
        }
    }
}