package com.lee.dateplanner.festival.network

import com.lee.dateplanner.BuildConfig
import com.lee.dateplanner.festival.data.FestivalInfoData
import com.lee.dateplanner.festival.data.FestivalSpaceData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST

const val FESTIVAL_ADDRESS = "http://openapi.seoul.go.kr:8088/${BuildConfig.FESTIVAL_KEY}/json/"

interface FestivalRetrofitService {
    @GET("culturalEventInfo/1/1000/") // 행사 정보 api
    suspend fun getFestivalInfo(): Response<FestivalInfoData>
    @GET("culturalSpaceInfo/1/819/") // 행사 장소 정보 api
    suspend fun getFestivalPlace(): Response<FestivalSpaceData>

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