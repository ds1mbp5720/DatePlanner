package com.lee.dateplanner.common

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.lee.dateplanner.map.adpter.POIWindowAdapter
import net.daum.mf.map.api.MapView
import java.util.*

// toast 사용 목적 함수
fun toastMessage(message: String){
    Toast.makeText(DatePlannerApplication.getAppInstance(),message,Toast.LENGTH_SHORT).show()
}
// 시간 string format
fun timeStringFormat(hour: Int, minute: Int): String{
    return if(minute < 10){ // minute이 한자리일 경우 0* 으로 보이게 변경
        "$hour:0${minute}"
    } else "$hour:${minute}"
}
// 날짜 string format
fun dateStringFormat(month: Int, day: Int):String{
    return "${month+1}월 ${day}일"  // month 0월 부터 시작함
}