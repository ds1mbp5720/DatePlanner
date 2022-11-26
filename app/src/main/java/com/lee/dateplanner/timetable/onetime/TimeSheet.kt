package com.lee.dateplanner.timetable.onetime

// 한타임 계획 data 양식
data class TimeSheet(
    var title: String, // 일정 제목
    var time: String, // 일정 시간
    var place: String, // 일정 장소
    var memo: String // 일정 메모
)
