package com.lee.dateplanner.map

import com.lee.dateplanner.festival.FESTIVAL_ADDRESS
import com.lee.dateplanner.festival.FestivalRetrofitService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST

const val POI_ADDRESS = "https://dapi.kakao.com/v2/local/search/category.json/"

interface POIRetrofitService {
    @POST("?category_group_code=CE7&y=37.514322572335935&x=127.06283102249932&radius=2000") // poi정보 정보 api
    fun getPOIInfo(): Call<ResponseBody>

    companion object {
        var retrofitService: POIRetrofitService? = null
        fun getInstance() : POIRetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(FESTIVAL_ADDRESS)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(POIRetrofitService::class.java)
            }
            return retrofitService!!
        }
    }
}