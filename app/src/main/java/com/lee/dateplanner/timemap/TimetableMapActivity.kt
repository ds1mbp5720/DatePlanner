package com.lee.dateplanner.timemap

import android.app.Activity
import android.os.Bundle
import com.lee.dateplanner.databinding.MyScheduleMapActivityLayoutBinding

/**
 * 시간 계획표 마커 표시 지도 activity
 */
class TimetableMapActivity:Activity() {
    private lateinit var binding: MyScheduleMapActivityLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MyScheduleMapActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}