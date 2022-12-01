package com.lee.dateplanner

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.tabs.TabLayout
import com.lee.dateplanner.databinding.ActivityMainBinding
import com.lee.dateplanner.festival.FestivalListFragment
import com.lee.dateplanner.map.AroundMapFragment
import com.lee.dateplanner.timemap.TimetableMapActivity
import com.lee.dateplanner.timetable.TimeTableFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var timeTableFragment: TimeTableFragment // 시간계획
    private lateinit var festivalListFragment: FestivalListFragment // 축제정보
    private lateinit var aroundMapFragment: AroundMapFragment // 주변 상권정보
    private lateinit var timeTableMapFragment: TimetableMapActivity // 시간계획 지도

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        // 최초 fragment instance 생성
        if(savedInstanceState == null){
            timeTableFragment = TimeTableFragment()
            festivalListFragment = FestivalListFragment()
            aroundMapFragment = AroundMapFragment()
            timeTableMapFragment = TimetableMapActivity()
        }
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout) // tab 연결
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val param = when(tab.position){
                    //tab 선택 위치에 따른 param 지정
                    0 -> {getString(R.string.main_tab_1)}
                    1 -> {getString(R.string.main_tab_2)}
                    2 -> {getString(R.string.main_tab_3)}
                    else -> throw IllegalAccessException("Unexpected value: " + tab.position)
                }
                val ft = supportFragmentManager.beginTransaction() // fragment 관리
                with(binding){
                    with(ft){
                        // 선택에 따른 fragment 이동
                        when(param){
                            getString(R.string.main_tab_1) -> {
                                replace(R.id.tabContent,timeTableFragment).commit()
                            }
                            getString(R.string.main_tab_2) -> {
                                replace(R.id.tabContent,festivalListFragment).commit()
                            }
                            getString(R.string.main_tab_3) -> {
                                replace(R.id.tabContent,aroundMapFragment).commit()
                            }
                            else -> throw IllegalAccessException("Unexpected value: " + tab.position)
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        //초기 보여질 화면
        supportFragmentManager.beginTransaction().replace(R.id.tabContent,timeTableFragment).commit()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}