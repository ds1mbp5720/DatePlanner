package com.lee.dateplanner.festival

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lee.dateplanner.base.BaseViewModel
import com.lee.dateplanner.base.SingleLiveEvent
import com.lee.dateplanner.common.getTodayDate
import com.lee.dateplanner.common.toastMessage
import com.lee.dateplanner.festival.data.FestivalInfoData
import com.lee.dateplanner.festival.data.FestivalSpaceData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * 행사정보 viewModel
 */
@HiltViewModel
class FestivalViewModel @Inject constructor(private var repository: FestivalRepository, application: Application):BaseViewModel(application) {
    //val festivalList = MutableLiveData<FestivalInfoData>()
    //val festivalPlaceList = MutableLiveData<FestivalSpaceData>()

    private val _festivalList = SingleLiveEvent<FestivalInfoData>()
    val festivalList: LiveData<FestivalInfoData> get() = _festivalList
    private val _festivalPlaceList = SingleLiveEvent<FestivalSpaceData>()
    val festivalPlaceList: LiveData<FestivalSpaceData> get() = _festivalPlaceList


    private var job: Job? = null

    fun getAllFestivalFromViewModel(category: String, year: Int=0 , month: Int=0, day: Int=0){
       runScope({
           repository.getFestivalInfo(
               category
           )
       }){
           _festivalList.value = it.body()
       }
    }
    fun getFestivalLocationFromViewModel(){
        runScope({
            repository.getFestivalPlace()
        }){
            _festivalPlaceList.value = it.body()
        }
        /*job = CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
            isLoading.postValue(true)
            val placeResponse = repository.getFestivalPlace() // 행사 장소
            withContext(Dispatchers.Main) {
                if(placeResponse.isSuccessful){
                    festivalPlaceList.postValue(placeResponse.body())
                }else{
                    onError("에러내용 : ${placeResponse.message()}")
                }
            }
        }*/
    }
    /*fun getFestivalFromViewModelPaging(category: String, year: Int=0 , month: Int=0, day: Int=0, start: Int, end: Int){
        job = CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
            isLoading.postValue(true)
            val infoResponse = repository.getPagingFestivalInfo(category,start, end) // 행사 정보
            withContext(Dispatchers.Main){
                if(infoResponse.isSuccessful){
                    festivalList.postValue(infoResponse.body())
                    isLoading.postValue(false)
                }else{
                    onError("에러내용 : ${infoResponse.message()}")
                }
            }
            withContext(Dispatchers.Main){
                if(year != 0 && month != 0 && day != 0){
                    festivalList.value!!.culturalEventInfo.row = filterByDate(year,month,day)
                }else{
                    festivalList.value!!.culturalEventInfo.row = filterByTodayDate()
                }
            }
        }
    }*/
    private fun onError(message: String){
        errorMessage.postValue(message)
        isLoading.postValue(false)
    }
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}