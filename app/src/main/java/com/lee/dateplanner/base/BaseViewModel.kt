package com.lee.dateplanner.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lee.dateplanner.common.toastMessage
import com.lee.dateplanner.festival.data.FestivalInfoData
import com.lee.dateplanner.map.data.POIData
import kotlinx.coroutines.*
import retrofit2.Response

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    val errorMessage = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()
    private var job : Job? = null

    protected fun <T> runScope(
        req: suspend CoroutineScope.() -> T,
        isProgress: Boolean = true,
        res: ((T) -> Unit)? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if(isProgress)
                isLoading.postValue(true)
            try{
                req().also { value ->
                    if(req is Response<*>){
                        launch(Dispatchers.Main) {
                            if(req.isSuccessful){
                                res?.invoke(value)
                                isLoading.postValue(false)
                            }else{
                                onError("에러내용: ${req.message()}")
                            }
                        }
                    }
                }
            }catch (e: java.lang.Exception){
                onError("예외 에러내용: $e")
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
   /* fun getAllFestivalFromViewModel(category: String, year: Int=0 , month: Int=0, day: Int=0){
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
                if(festivalList.value!!.culturalEventInfo.row.isEmpty()){
                    toastMessage("예정된 행사가 없습니다.")
                }
            }
        }
    }*/
}