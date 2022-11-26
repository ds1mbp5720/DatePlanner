package com.lee.dateplanner.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lee.dateplanner.festival.FestivalRepository
import com.lee.dateplanner.festival.FestivalViewModel

@Suppress("UNCHECKED_CAST")
class TimeTableViewModelFactory (private var repository: TimetableRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(TimetableViewModel::class.java)) {
            TimetableViewModel(repository) as T
        } else {
            throw IllegalArgumentException("해당 뷰모델 못찾을수 없습니다.")
        }
    }
}