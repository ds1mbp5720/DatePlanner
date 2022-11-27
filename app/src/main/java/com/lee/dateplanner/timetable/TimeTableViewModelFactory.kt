package com.lee.dateplanner.timetable

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class TimeTableViewModelFactory (private var repository: TimetableRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(TimetableViewModel::class.java)) {
            TimetableViewModel(application = Application()) as T
        } else {
            throw IllegalArgumentException("해당 뷰모델 못찾을수 없습니다.")
        }
    }
}