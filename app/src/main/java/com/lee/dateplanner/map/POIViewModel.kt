package com.lee.dateplanner.map

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lee.dateplanner.map.data.POIData
import kotlinx.coroutines.*

class POIViewModel(private val repository:POIRepository):ViewModel() {

    val poiList = MutableLiveData<POIData>() // rest api 저장
    val errorMessage = MutableLiveData<String>()

    val isLoading = MutableLiveData<Boolean>()
    private var job : Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("코루틴내 예외: ${throwable.localizedMessage}")
    }

    fun getAllPoiFromViewModel(){
        job = CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
            isLoading.postValue(true)
            val response = repository.getPOIInfo()
            withContext(Dispatchers.Main){
                if(response.isSuccessful){
                    poiList.postValue(response.body())
                    //isLoading.value = false
                }else{
                    onError("에러내용:  $response")
                }
            }
            for(i in 0 until poiList.value!!.documents!!.size){
                poiList.value!!.documents[i].categoryName = filterCategory(poiList.value!!.documents[i].categoryName)
            }
        }
    }
    // 카테고리 문자열 마지막만 남기는 함수
    fun filterCategory(category: String): String{
        val categoryList: MutableList<String> = category.split(">") as MutableList<String>
        return categoryList[categoryList.size-1]
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