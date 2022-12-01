package com.lee.dateplanner.common

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.lee.dateplanner.timetable.InsertTimeTableActivity
import java.util.*

// toast 사용 목적 함수
fun toastMessage(message: String){
    Toast.makeText(DatePlannerApplication.getAppInstance(),message,Toast.LENGTH_SHORT).show()
}
// 시간 dialog 함수
fun makeTimePickerDialog(activity: AppCompatActivity): String{
    var text:String = ""
    val cal = Calendar.getInstance()
    val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
        text = "$hourOfDay:$minute"
        Log.e(TAG,"내부 $text")
    }
    TimePickerDialog(activity,timeSetListener,cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), android.text.format.DateFormat.is24HourFormat(activity)).show()
    return text
}
// 날짜 dialog 함수
fun makeDatePickerDialog(fragment: Fragment): String{
    var text:String = ""
    val cal = Calendar.getInstance()
    val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        text = "$month 월 $dayOfMonth 일"
        Log.e(TAG,"진입 $text")
    }
    fragment.context?.let { DatePickerDialog(it,dateSetListener,cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show() }
    return text
}