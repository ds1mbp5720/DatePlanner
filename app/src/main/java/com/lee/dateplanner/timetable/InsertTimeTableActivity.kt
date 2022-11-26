package com.lee.dateplanner.timetable

import android.app.Activity
import android.os.Bundle
import com.lee.dateplanner.databinding.InputScheduleLayoutBinding

/**
 * 시간계획 입력 창
 */
class InsertTimeTableActivity: Activity() {
    private lateinit var binding: InputScheduleLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = InputScheduleLayoutBinding.inflate(layoutInflater).also {
            setContentView(binding.root)
        }
    }
}