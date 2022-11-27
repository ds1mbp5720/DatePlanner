package com.lee.dateplanner.timetable

import android.app.Activity
import android.os.Bundle
import com.lee.dateplanner.databinding.FestivalWebviewActivityBinding
import com.lee.dateplanner.databinding.InputScheduleLayoutBinding

/**
 * 시간계획 입력 창
 */
class InsertTimeTableActivity: Activity() {
    private lateinit var binding: InputScheduleLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = InputScheduleLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //뒤로가기 선택시
        binding.inputBackBtn.setOnClickListener {
            finish()
        }
        //등록 선택시
        binding.insertBtn.setOnClickListener {

        }
    }
}