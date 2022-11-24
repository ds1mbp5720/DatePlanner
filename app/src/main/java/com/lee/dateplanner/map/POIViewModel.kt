package com.lee.dateplanner.map

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lee.dateplanner.map.data.POIData
import kotlinx.coroutines.*

class POIViewModel(private val repository:POIRepository):ViewModel() {

    val poiList = MutableLiveData<POIData>()
    val errorMessage = MutableLiveData<String>()

    val isLoading = MutableLiveData<Boolean>()
    private var job : Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("코루틴내 예외: ${throwable.localizedMessage}")
    }

    fun getAllPoiFromViewModel(){
        job = CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
            //isLoading.value = true
            isLoading.postValue(true)

            val response = repository.getPOIInfo()
            withContext(Dispatchers.Main){
                if(response.isSuccessful){
                    poiList.postValue(response.body())
                    Log.e("TAG", "doIs")
                    Log.e("TAG" , "짠 : ${poiList.value}")
                    //poiList.value = response.body()
                    //isLoading.value = false
                }else{
                    onError("에러내용:  $response")
                }
            }
        }
    }

    private fun onError(message: String){
        errorMessage.postValue(message)
        //isLoading.postValue(false)
    }
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}