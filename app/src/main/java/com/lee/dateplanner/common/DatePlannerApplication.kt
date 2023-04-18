package com.lee.dateplanner.common

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import android.os.Bundle
import com.lee.dateplanner.BuildConfig
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.NaverMapSdk.NaverCloudPlatformClient
import dagger.hilt.android.HiltAndroidApp


/**
 * 최초 실행코드
 * App scope class,file 에서 호출할수있는 코드
 */
@HiltAndroidApp
class DatePlannerApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        appInstance = this
        settingScreenPortrait()
        //NaverMapSdk.getInstance(this).client = NaverCloudPlatformClient(BuildConfig.NAVER_CLIENT_ID)
    }
    companion object{
        private lateinit var appInstance: DatePlannerApplication
        fun getAppInstance() = appInstance
    }
    private fun settingScreenPortrait(){
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks{
            @SuppressLint("SourceLockedOrientationActivity")
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            // 모든 activity 에서 재정의 해야할 부분 있을시
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}