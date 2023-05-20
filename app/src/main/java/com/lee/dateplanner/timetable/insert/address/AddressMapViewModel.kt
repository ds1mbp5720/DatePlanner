package com.lee.dateplanner.timetable.insert.address

import android.app.Application
import androidx.lifecycle.LiveData
import com.lee.dateplanner.base.BaseViewModel

class AddressMapViewModel(application: Application): BaseViewModel(application) {
    private val _event = com.lee.dateplanner.base.SingleLiveEvent<Event>()
    val event: LiveData<Event> get() = _event
    fun onEventClick(event: Event){
        _event.value = event
    }

    sealed class Event {
        object Close : Event()
        object Save : Event()
    }
}