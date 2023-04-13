package com.lee.dateplanner.base

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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
                    }else{
                        // req 타입에 따라 재 확인 필요
                        launch(Dispatchers.Main) {
                            res?.invoke(value)
                            isLoading.postValue(false)
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
        Log.e("","에러내용 $message")
        isLoading.postValue(false)
    }
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}