package com.lee.dateplanner.timetable.onetime


/**
 * 일일단위 timetable 에서 계획  1개 data 양식
 */
data class TimeSheet(
    var title: String, // 일정 제목
    var time: String, // 일정 시간
    var place: String, // 일정 장소
    var memo: String, // 일정 메모
    var lat: String, // 장소 위도
    var lgt: String  // 장소 경도
)
