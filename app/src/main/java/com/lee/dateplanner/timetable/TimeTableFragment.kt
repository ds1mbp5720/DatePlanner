package com.lee.dateplanner.timetable

import android.annotation.SuppressLint
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
import com.lee.dateplanner.R
import com.lee.dateplanner.databinding.TimetablelistFragmentLayoutBinding
import com.lee.dateplanner.timetable.onetime.TimeSheet
import com.lee.dateplanner.timetable.time.adapter.TimetableRecyclerAdapter
import com.lee.dateplanner.timetable.time.room.Timetable

class TimeTableFragment:Fragment() {
    companion object{
        fun newInstance() = TimeTableFragment()
    }
    private val viewModel: TimetableViewModel by viewModels()  // 뷰모델
    private var timetableAdapter: TimetableRecyclerAdapter? = null  // 시간계획 recyclerview adapter
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
            var emptyTimeSheet = TimeSheet("","","","","","")
            timeSheetList.add(emptyTimeSheet)
            var date = "00.00.(일)"

            viewModel.insertTimeTable(Timetable(0,timeSheetList,date))
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
     * ui셋팅 함수
     */
    private fun uiSetup(){
        with(binding.allTimeTable){
            timetableAdapter = TimetableRecyclerAdapter(R.layout.timetable_recycler_layout)
            adapter = timetableAdapter
        }
    }
}