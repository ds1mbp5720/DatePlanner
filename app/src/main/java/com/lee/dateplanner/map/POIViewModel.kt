package com.lee.dateplanner.map

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
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
    fun getAllPoiFromViewModel(category: String, lat:String, lgt:String){
        job = CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
            isLoading.postValue(true)
            val response = repository.getPOIInfo(category, lat, lgt) // 기준점, 카테고리 전달
            withContext(Dispatchers.Main){
                if(response.isSuccessful){
                    poiList.postValue(response.body())
                    isLoading.postValue(false)
                }else{
                    onError("에러내용:  $response")
                }
            }
            // 카테고리 중간 분류들 생략 후 최종 세부 카테고리만 저장
            for(i in 0 until poiList.value!!.documents!!.size){
                poiList.value!!.documents[i].categoryName = filterCategory(poiList.value!!.documents[i].categoryName)
            }
        }
    }
    // 카테고리 문자열 마지막만 남기는 함수
    fun filterCategory(category: String): String{
        // > 를 통해 카테고리 단계 세분화로 >를 통한 분리 후 저장
        val categoryList: MutableList<String> = category.split(">") as MutableList<String>
        return categoryList[categoryList.size-1]
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