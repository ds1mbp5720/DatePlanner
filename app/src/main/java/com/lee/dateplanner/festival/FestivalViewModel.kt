package com.lee.dateplanner.festival

import android.app.Application
import androidx.lifecycle.LiveData
import com.lee.dateplanner.base.BaseViewModel
import com.lee.dateplanner.base.SingleLiveEvent
import com.lee.dateplanner.festival.data.FestivalInfoData
import com.lee.dateplanner.festival.data.FestivalSpaceData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 행사정보 viewModel
 */
@HiltViewModel
class FestivalViewModel @Inject constructor(
    private var repository: FestivalRepository,
    application: Application
) : BaseViewModel(application) {
    private val _festivalList = SingleLiveEvent<FestivalInfoData>()
    val festivalList: LiveData<FestivalInfoData> get() = _festivalList
    private val _festivalPlaceList = SingleLiveEvent<FestivalSpaceData>()
    val festivalPlaceList: LiveData<FestivalSpaceData> get() = _festivalPlaceList
    private val _eventClick = SingleLiveEvent<Event>()
    val eventClick: LiveData<Event> get() = _eventClick
    private val _getLocationCheck = SingleLiveEvent<Boolean>()
    val getLocationCheck: LiveData<Boolean> get() = _getLocationCheck

    fun onEventClick(event: Event){
        _eventClick.value = event
    }
    fun onGetLocationCheck(check : Boolean){
        if(check) _getLocationCheck.postValue(true)
        else _getLocationCheck.postValue(false)
    }

    fun getAllFestivalFromViewModel(category: String, year: Int = 0, month: Int = 0, day: Int = 0) {
        runScope({
            repository.getFestivalInfo(
                category
            )
        }) {
            _festivalList.value = it.body()
        }
    }
    fun getFestivalLocationFromViewModel() {
        runScope({
            repository.getFestivalPlace()
        }) {
            _festivalPlaceList.value = it.body()
        }
    }

    sealed class Event {
        object Date : Event()
        object Category : Event()
    }
}