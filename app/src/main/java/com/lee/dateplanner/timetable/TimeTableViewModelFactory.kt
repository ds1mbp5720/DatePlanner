package com.lee.dateplanner.timetable

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lee.dateplanner.R

@Suppress("UNCHECKED_CAST")
class TimeTableViewModelFactory (private var repository: TimetableRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(TimetableViewModel::class.java)) {
            TimetableViewModel(application = Application()) as T
        } else {
            throw IllegalArgumentException(R.string.notFindViewModel.toString())
        }
    }
}