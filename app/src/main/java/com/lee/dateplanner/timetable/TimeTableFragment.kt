package com.lee.dateplanner.timetable

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.lee.dateplanner.MainActivity
import com.lee.dateplanner.R
import com.lee.dateplanner.databinding.TimetablelistFragmentLayoutBinding
import com.lee.dateplanner.map.POIMapFragment
import com.lee.dateplanner.timetable.timesheet.TimeSheet
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
    var timetableAdapter: TimetableRecyclerAdapter? = null  // 시간계획 recyclerview adapter
    private lateinit var binding: TimetablelistFragmentLayoutBinding

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

        listenerSetup()
        observerSetup()
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
             * 계획 추가 버튼을 dialog 로 하여 선택한 날짜를 바로 가져와서 변수에 넣기
             */
            var date = "날짜를 수정하세요."
            // room db 추가
            viewModel.insertTimeTable(Timetable(timeSheetList ,date))
        }
    }

    /**
     * 옵저버 셋팅
     */
    @SuppressLint("SetTextI18n")
    private fun observerSetup(){
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