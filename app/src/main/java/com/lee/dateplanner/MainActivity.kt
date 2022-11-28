package com.lee.dateplanner

import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.google.android.material.tabs.TabLayout
import com.lee.dateplanner.databinding.ActivityMainBinding
import com.lee.dateplanner.festival.FestivalListFragment
import com.lee.dateplanner.map.AroundMapFragment
import com.lee.dateplanner.timetable.TimeTableFragment
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var timeTableFragment: TimeTableFragment
    private lateinit var festivallistFragment: FestivalListFragment
    private lateinit var aroundMapFragment: AroundMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        if(savedInstanceState == null){
            timeTableFragment = TimeTableFragment()
            festivallistFragment = FestivalListFragment()
            aroundMapFragment = AroundMapFragment()
        }
        //초기 보여질 화면
        supportFragmentManager.beginTransaction().replace(R.id.tabContent,timeTableFragment).commit()

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout) // tab 연결
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val param = when(tab.position){
                    0 -> {"시간계획"}
                    1 -> {"축제 목록"}
                    2 -> {"주변정보 지도"}
                    else -> throw IllegalAccessException("Unexpected value: " + tab.position)
                }
                val ft = supportFragmentManager.beginTransaction()
                with(binding){
                    with(ft){
                        when(param){
                            "시간계획" -> {
                                replace(R.id.tabContent,timeTableFragment).commit()
                            }
                            "축제 목록" -> {
                                replace(R.id.tabContent,festivallistFragment).commit()
                            }
                            "주변정보 지도" -> {
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

    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}