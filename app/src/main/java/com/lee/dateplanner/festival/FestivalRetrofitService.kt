package com.lee.dateplanner.festival

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST

const val FESTIVAL_ADDRESS = "http://openapi.seoul.go.kr:8088/4e4e7365496473313639784864686f/json/"

interface FestivalRetrofitService {
    @POST("culturalEventInfo/1/1000/") // 행사 정보 api
    fun getFestivalInfo(): Call<ResponseBody>

    @POST("culturalSpaceInfo/1/819/") // 행사 장소 정보 api
    fun getFestivalPlace(): Call<ResponseBody>

    companion object {
        var retrofitService: FestivalRetrofitService? = null
        fun getInstance() : FestivalRetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(FESTIVAL_ADDRESS)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(FestivalRetrofitService::class.java)
            }
            return retrofitService!!
        }
    }
}