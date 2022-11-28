package com.lee.dateplanner.festival

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lee.dateplanner.festival.data.FestivalInfoData
import com.lee.dateplanner.festival.data.FestivalSpaceData
import kotlinx.coroutines.*

class FestivalViewModel(private var repository: FestivalRepository):ViewModel() {

    val festivalList = MutableLiveData<FestivalInfoData>()
    val festivalPlaceList = MutableLiveData<FestivalSpaceData>()
    val errorMessage = MutableLiveData<String>()

    val isLoading = MutableLiveData<Boolean>()
    private var job: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, thrownException ->
        onError("코루틴내 예외: ${thrownException.localizedMessage}")
    }


    fun getAllFestivalFromViewModel(){
        job = CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
            isLoading.postValue(true)
            val infoResponse = repository.getFestivalInfo() // 행사 정보
            val placeResponse = repository.getFestivalPlace() // 행사 장소
            withContext(Dispatchers.Main){
                if(infoResponse.isSuccessful){
                    festivalList.postValue(infoResponse.body())
                    isLoading.postValue(false)
                }else{
                    onError("에러내용 : ${infoResponse.message()}")
                }
                if(placeResponse.isSuccessful){
                    festivalPlaceList.postValue(placeResponse.body())
                }else{
                    onError("에러내용 : ${placeResponse.message()}")
                }
            }
        }
    }

    private fun onError(message: String){
        errorMessage.value = message
        isLoading.postValue(false)
    }
    override fun onCleared() {
        super.onCleared()
    }
}