package com.lee.dateplanner.timetable

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jakewharton.rxbinding4.view.clicks
import com.lee.dateplanner.R
import com.lee.dateplanner.base.BaseFragment
import com.lee.dateplanner.common.dateStringFormat
import com.lee.dateplanner.common.filterByDate
import com.lee.dateplanner.common.makeDatePickerDialog
import com.lee.dateplanner.common.toast
import com.lee.dateplanner.databinding.TimetablelistFragmentLayoutBinding
import com.lee.dateplanner.timetable.timesheet.TimeSheet
import com.lee.dateplanner.timetable.time.adapter.TimetableRecyclerAdapter
import com.lee.dateplanner.timetable.time.room.Timetable
import dagger.hilt.android.AndroidEntryPoint

/**
 * 저장된 시간계획 제공 fragment
 */
@AndroidEntryPoint
class TimeTableFragment:BaseFragment<TimetablelistFragmentLayoutBinding,TimetableViewModel>() {
    companion object{
        fun newInstance() = TimeTableFragment()
    }
    override val layoutId: Int = R.layout.timetablelist_fragment_layout
    override val viewModel: TimetableViewModel by viewModels()
    var timetableAdapter: TimetableRecyclerAdapter? = null  // 시간계획 recyclerview adapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    override fun initObserve() {
        super.initObserve()
        viewModel.getAllTimetables()?.observe(this){ timetable ->
            if(timetable.isEmpty()){
                dataBinding.emptyTimetable.visibility = View.VISIBLE
            }else{
                dataBinding.emptyTimetable.visibility = View.INVISIBLE
            }
            timetable?.let {
                timetableAdapter?.setTimetableList(it)
            }
        }
    }
    override fun initViews() {
        super.initViews()
        with(dataBinding.allTimeTable){
            timetableAdapter = TimetableRecyclerAdapter(viewModel,this@TimeTableFragment)
            adapter = timetableAdapter
        }
        dataBinding.addBtn.clicks().subscribe {
            val timeSheetList =  mutableListOf<TimeSheet>()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, _, month, dayOfMonth ->
                //val date = getString(R.string.fixDateMessage)
                val date = dateStringFormat(month, dayOfMonth)
                // room db 추가
                viewModel.insertTimeTable(Timetable(timeSheetList ,date))
            }
            this.context?.let { it1 -> makeDatePickerDialog(it1, dateSetListener,"계획 추가") }
        }
    }
}