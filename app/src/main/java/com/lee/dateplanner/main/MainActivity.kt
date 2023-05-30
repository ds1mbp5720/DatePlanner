package com.lee.dateplanner.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.plusAssign
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.lee.dateplanner.R
import com.lee.dateplanner.base.BaseActivity
import com.lee.dateplanner.databinding.ActivityMainBinding
import com.lee.dateplanner.dialog.MessageDialog
import com.lee.dateplanner.festival.FestivalListFragment
import com.lee.dateplanner.navigation.ShowHideNavigation
import com.lee.dateplanner.poimap.POIMapFragment
import com.lee.dateplanner.timetable.TimeTableFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    override val layoutId: Int = R.layout.activity_main
    override val viewModel: MainViewModel by viewModels()

    private lateinit var timeTableFragment: TimeTableFragment // 시간계획
    private lateinit var festivalListFragment: FestivalListFragment // 축제정보
    private lateinit var poiMapFragment: POIMapFragment // 주변 상권정보

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 최초 fragment instance 생성
        createFragment(savedInstanceState)
        val navHostFragment = supportFragmentManager.findFragmentById(dataBinding.fcvFragmentContainer.id) as NavHostFragment
        val navController = navHostFragment.navController

        val navigator = ShowHideNavigation(this, navHostFragment.childFragmentManager, dataBinding.fcvFragmentContainer.id)
        navController.navigatorProvider += navigator

        navController.setGraph(R.navigation.main_nav_graph)
        dataBinding.bnvBottomNavigation.setupWithNavController(navController)
    }
    private fun createFragment(savedInstanceState: Bundle?){
        if(savedInstanceState == null){
            timeTableFragment = TimeTableFragment()
            festivalListFragment = FestivalListFragment()
            poiMapFragment = POIMapFragment()
        }
    }

    private fun setListener(tabLayout: TabLayout){
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
                with(ft){
                    // 선택에 따른 fragment 이동
                    when(param){
                        getString(R.string.main_tab_1) -> {
                            hide(poiMapFragment)
                            hide(festivalListFragment)
                            show(timeTableFragment).commit()
                        }
                        getString(R.string.main_tab_2) -> {
                            hide(poiMapFragment)
                            hide(timeTableFragment)
                            show(festivalListFragment).commit()
                        }
                        getString(R.string.main_tab_3) -> {
                            hide(timeTableFragment)
                            hide(festivalListFragment)
                            show(poiMapFragment).commit()
                        }
                        else -> throw IllegalAccessException("Unexpected value: " + tab.position)
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if(poiMapFragment.behavior.state == BottomSheetBehavior.STATE_EXPANDED)
            poiMapFragment.behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        else{
            MessageDialog(getString(R.string.destroy_app),getString(R.string.check),getString(R.string.cancel)).onRightBtn{
                super.onBackPressed()
                finish()
                finishAndRemoveTask() // 완전히 종료
            }.show(supportFragmentManager,"")
        }

    }
}