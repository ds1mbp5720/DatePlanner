package com.lee.dateplanner.festival

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lee.dateplanner.festival.data.FestivalInfoData
import kotlinx.coroutines.*

class FestivalViewModel(private var repository: FestivalRepository):ViewModel() {

    val festivalList = MutableLiveData<List<FestivalInfoData>>()
    val errorMessage = MutableLiveData<String>()

    val isLoading = MutableLiveData<Boolean>()
    private var job: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, thrownException ->
        onError("코루틴내 예외: ${thrownException.localizedMessage}")
    }
    private fun onError(message: String){
        errorMessage.value = message
        //isLoading.value = false
    }

    fun getAllFestivalFromViewModel(){
        job = CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
            //isLoading.postValue(true)
            val response = repository.getFestivalInfo()
            withContext(Dispatchers.Main){
                if(response.isSuccessful){
                    festivalList.postValue(response.body())
                    //isLoading.value = false
                }else{
                    onError("에러내용 : ${response.message()}")
                }
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
    }
}