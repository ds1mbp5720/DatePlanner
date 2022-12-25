package com.lee.dateplanner.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lee.dateplanner.map.data.POIData
import kotlinx.coroutines.*

/**
 * 주변 상권정보 제공 viewModel
 */
class POIViewModel(private val repository:POIRepository):ViewModel() {

    val poiList = MutableLiveData<POIData>() // rest api 저장
    val errorMessage = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()
    private var job : Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("코루틴내 예외: ${throwable.localizedMessage}")
    }
    //rest 포이정보 호출하여 get 함수
    // 이때 category -> 식당, 카페, 놀거리와 기준점 되는 행사장 정보 인자
    fun getAllPoiFromViewModel(category: String, lat:String, lgt:String,page: Int){
        job = CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
            isLoading.postValue(true)
            val response = repository.getPOIInfo(category, lat, lgt,page,15) // 기준점, 카테고리 전달
            withContext(Dispatchers.Main){
                if(response.isSuccessful){
                    poiList.postValue(response.body())
                    isLoading.postValue(false)
                }else{
                    onError("에러내용:  $response")
                }
            }
        }
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