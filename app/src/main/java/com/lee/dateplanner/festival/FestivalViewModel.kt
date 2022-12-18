package com.lee.dateplanner.festival

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
class FestivalViewModel @Inject constructor(private var repository: FestivalRepository):ViewModel() {
    val festivalList = MutableLiveData<FestivalInfoData>()
    val festivalPlaceList = MutableLiveData<FestivalSpaceData>()
    val errorMessage = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()
    private var job: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, thrownException ->
        onError("코루틴내 예외: ${thrownException.localizedMessage}")
    }

    fun getAllFestivalFromViewModel(category: String, year: Int=0 , month: Int=0, day: Int=0){
        job = CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
            isLoading.postValue(true)
            val infoResponse = repository.getFestivalInfo(category) // 행사 정보
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
                if(festivalList.value!!.culturalEventInfo.row.size == 0){
                    toastMessage("예정된 행사가 없습니다.")
                }
            }
        }
    }
    fun getFestivalFromViewModelPaging(category: String, year: Int=0 , month: Int=0, day: Int=0, start: Int, end: Int){
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
    }
    fun getFestivalLocationFromViewModel(){
        job = CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
            isLoading.postValue(true)
            val placeResponse = repository.getFestivalPlace() // 행사 장소
            withContext(Dispatchers.Main) {
                if(placeResponse.isSuccessful){
                    festivalPlaceList.postValue(placeResponse.body())
                }else{
                    onError("에러내용 : ${placeResponse.message()}")
                }
            }
        }
    }
    // 날짜 지난 행사들 제외
    private fun filterByTodayDate(): MutableList<FestivalInfoData.CulturalEventInfo.Row>{
        val festivalRowList = mutableListOf<FestivalInfoData.CulturalEventInfo.Row>()
        for(i in 0 until  festivalList.value?.culturalEventInfo?.row!!.size){
            val endDate = filterFestivalDateInt(festivalList.value?.culturalEventInfo?.row!![i].eNDDATE)
            if(endDate >= getTodayDate()){
                festivalRowList.add(festivalList.value!!.culturalEventInfo.row[i])
            }
        }
        return festivalRowList
    }
    private fun filterByDate(year: Int, month: Int, day: Int): MutableList<FestivalInfoData.CulturalEventInfo.Row>{
        val insertDate = filterInsertDateInt(year,month, day)
        val festivalRowList = mutableListOf<FestivalInfoData.CulturalEventInfo.Row>()
        for(i in 0 until  festivalList.value?.culturalEventInfo?.row!!.size){
            val startDate = filterFestivalDateInt(festivalList.value?.culturalEventInfo?.row!![i].sTRTDATE)
            val endDate = filterFestivalDateInt(festivalList.value?.culturalEventInfo?.row!![i].eNDDATE)
            if(insertDate in startDate..endDate)
                festivalRowList.add(festivalList.value!!.culturalEventInfo.row[i])
        }
        return festivalRowList
    }
    private fun filterFestivalDateInt(date: String): Int {
        val filterToList = date.split("-", " ") as MutableList<String>
        return (filterToList[0] + filterToList[1] + filterToList[2]).toInt()
    }
    private fun filterInsertDateInt(year: Int, month: Int, day: Int): Int{
        val setMonth = if(month<10){ "0$month" }else month.toString()
        val setDay = if(day<10){ "0$day" }else day.toString()
        return (year.toString() + setMonth + setDay).toInt()
    }
    private fun onError(message: String){
        errorMessage.postValue(message)
        isLoading.postValue(false)
    }
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}