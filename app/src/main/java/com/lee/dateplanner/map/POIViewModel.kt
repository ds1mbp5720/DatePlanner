package com.lee.dateplanner.map

import android.app.Application
import androidx.lifecycle.LiveData
import com.lee.dateplanner.base.BaseViewModel
import com.lee.dateplanner.base.SingleLiveEvent
import com.lee.dateplanner.map.data.POIData
import javax.inject.Inject

/**
 * 주변 상권정보 제공 viewModel
 */
class POIViewModel@Inject constructor(
    private val repository:POIRepository,
    application: Application) : BaseViewModel(application) {

    private val _poiList = SingleLiveEvent<POIData>()
    val poiList: LiveData<POIData> get() = _poiList
    private val _eventClick = SingleLiveEvent<Event>()
    val eventClick: LiveData<Event> get() = _eventClick
    fun onEventClick(event: Event){
        _eventClick.value = event
    }
    //rest 포이정보 호출하여 get 함수, 이때 category -> 식당, 카페, 놀거리와 기준점 되는 행사장 정보 인자
    fun getAllPoiFromViewModel(category: String, lat:String, lgt:String,page: Int){
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