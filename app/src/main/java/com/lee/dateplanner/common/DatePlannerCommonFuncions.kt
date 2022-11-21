package com.lee.dateplanner.common

import android.widget.Toast

// toast 사용 목적 함수
fun toastMessage(message: String){
    Toast.makeText(DatePlannerApplication.getAppInstance(),message,Toast.LENGTH_SHORT).show()
}