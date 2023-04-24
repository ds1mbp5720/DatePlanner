package com.lee.dateplanner.poimap

import android.app.Application
import androidx.lifecycle.LiveData
import com.lee.dateplanner.base.BaseViewModel
import com.lee.dateplanner.base.SingleLiveEvent
import com.lee.dateplanner.poimap.data.POIData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 주변 상권정보 제공 viewModel
 */
@HiltViewModel
class POIViewModel @Inject constructor(
    private var repository: POIRepository,
    application: Application
) : BaseViewModel(application) {
    private val _poiList = SingleLiveEvent<POIData>()
    val poiList: LiveData<POIData> get() = _poiList
    private val _eventClick = SingleLiveEvent<Event>()
    val eventClick: LiveData<Event> get() = _eventClick

    fun onEventClick(event: Event){
        _eventClick.value = event
    }

    fun getAllPoiFromViewModel(category: String, lat:String, lgt:String, page: Int){
        runScope({
            repository.getPOIInfo(
                category, lat, lgt, page, 15
            )
        }) {
            _poiList.value = it.body()
        }
    }

    sealed class Event {
        object Restaurant : Event()
        object Cafe : Event()
        object Enjoy : Event()
    }
}