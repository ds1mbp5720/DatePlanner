package com.lee.dateplanner.main

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.bumptech.glide.manager.SupportRequestManagerFragment
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
    private var navHostFragment = NavHostFragment()
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 최초 fragment instance 생성
        //createFragment(savedInstanceState)
        navHostFragment = supportFragmentManager.findFragmentById(dataBinding.fcvFragmentContainer.id) as NavHostFragment
        navController = navHostFragment.navController

        //todo 버전 2.3.5 이하로 해야 custom nav가 기능함
       /* navController.navigatorProvider.apply {
            this.addNavigator(
                ShowHideNavigation(this@MainActivity, navHostFragment.childFragmentManager, dataBinding.fcvFragmentContainer.id)
            )
        }*/
        navController.setGraph(R.navigation.main_nav_graph)

        dataBinding.bnvBottomNavigation.setupWithNavController(navController)

        /*appBarConfiguration = AppBarConfiguration(
            setOf(R.id.timetable_graph, R.id.festival_graph,  R.id.poi_graph)
        )*/
       /* dataBinding.bnvBottomNavigation.setOnItemSelectedListener { item ->
            NavigationUI.onNavDestinationSelected(item,navController)
            return@setOnItemSelectedListener true
        }*/
        dataBinding.bnvBottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.timeTableFragment -> {
                    Log.e("","생성된 화면 ${navController.currentDestination}")
                    val navOption = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setRestoreState(true)
                        .setPopUpTo(
                            navController.currentDestination!!.route // .graph.findStartDestination().id
                            ,inclusive = false,
                            saveState = true
                        ).build()
                    navController.navigate(R.id.move_to_timetable,null,navOption)
                }
                R.id.festivalListFragment -> {
                    Log.e("","생성된 화면 ${navController.currentDestination}")
                    val navOption = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setRestoreState(true)
                        .setPopUpTo(navController.currentDestination!!.route //navController.graph.findStartDestination().id
                            ,inclusive = false,
                            saveState = true
                        ).build()
                    navController.navigate(R.id.move_to_festival,null,navOption)
                }
                R.id.poiMapFragment -> {
                    Log.e("","생성된 화면 ${navController.currentDestination}")
                    val navOption = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setRestoreState(true)
                        .setPopUpTo(navController.currentDestination!!.route //navController.graph.findStartDestination().id
                            ,inclusive = false,
                            saveState = true
                        ).build()
                    navController.navigate(R.id.move_to_poi,null,navOption)
                }
            }
            true
        }
    }

    /*override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }*/
    /*private fun createFragment(savedInstanceState: Bundle?){
        if(savedInstanceState == null){
            timeTableFragment = TimeTableFragment()
            festivalListFragment = FestivalListFragment()
            poiMapFragment = POIMapFragment()
        }
    }*/
   /* private fun setListener(tabLayout: TabLayout){
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
    }*/

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        supportFragmentManager.fragments.forEach {
            Log.e("","생성된 화면 ${it}")
        }
        Log.e("","마지막 화면 ${navHostFragment.navController.currentDestination?.label}")
        Log.e("","생성된 화면 ${navHostFragment.fragmentManager?.fragments?.size}")
        //super.onBackPressed()
        /*if(poiMapFragment.behavior.state == BottomSheetBehavior.STATE_EXPANDED)
            poiMapFragment.behavior.state = BottomSheetBehavior.STATE_COLLAPSED*/
        if(navHostFragment.navController.currentDestination?.label == "TimeTableFragment"){
            MessageDialog(getString(R.string.destroy_app),getString(R.string.check),getString(R.string.cancel)).onRightBtn{
                super.onBackPressed()
                finish()
                finishAndRemoveTask() // 완전히 종료
            }.show(supportFragmentManager,"")
            Log.e("","마지막 화면 맞춤")
        }
        else{
            MessageDialog(getString(R.string.destroy_app),getString(R.string.check),getString(R.string.cancel)).onRightBtn{
                super.onBackPressed()
                finish()
                finishAndRemoveTask() // 완전히 종료
            }.show(supportFragmentManager,"")
        }
    }
}