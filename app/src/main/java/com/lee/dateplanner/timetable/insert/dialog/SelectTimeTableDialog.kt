package com.lee.dateplanner.timetable.insert.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import com.lee.dateplanner.databinding.SelectTimetableDialogBinding
import com.lee.dateplanner.timetable.TimetableViewModel
import com.lee.dateplanner.timetable.insert.InsertTimeSheetActivity
import com.lee.dateplanner.timetable.insert.adapter.SelectTimeTableAdapter

class SelectTimeTableDialog(private val message: String, private val viewModel: TimetableViewModel,
                            private var owner: InsertTimeSheetActivity)
    : Dialog(owner) {
    private lateinit var binding: SelectTimetableDialogBinding
    private var selectTimetableAdapter: SelectTimeTableAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE) // inflate 전에 선언
        binding = SelectTimetableDialogBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        binding.selectDialogMessage.text = message
        uiSetup()
        observerSetup(viewModel)
    }
    //옵저버 셋팅
    @SuppressLint("SetTextI18n")
    private fun observerSetup(viewModel: TimetableViewModel){
        viewModel.getAllTimetables()?.observe(owner){ timetable ->
            timetable?.let {
                selectTimetableAdapter?.setTimetableList(it)
            }
        }
    }
    //ui 셋팅 함수
    private fun uiSetup(){
        with(binding.selectListTimetable){
            selectTimetableAdapter = SelectTimeTableAdapter(owner,this@SelectTimeTableDialog)
            adapter = selectTimetableAdapter
        }
    }
}