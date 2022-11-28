package com.lee.dateplanner.map.network


import com.lee.dateplanner.BuildConfig
import com.lee.dateplanner.map.data.POIData
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

const val POI_ADDRESS = "https://dapi.kakao.com/"

interface POIRetrofitService {

    @Headers("Authorization:KakaoAK ${BuildConfig.KAKAO_REST_KEY}")
    //@GET("v2/local/search/category.json?category_group_code={code}&y={lat}&x={lng}&radius={radius}") // poi 정보 api
    @GET("v2/local/search/category.json") // poi 정보 api
    //@GET("v2/local/search/category.json?category_group_code=CE7&y=37.5143225723&x=127.062831022&radius=2000") // poi 정보 api
    suspend fun getPOIInfo(
        @Query("category_group_code") category_group_code: String, // poi 종류 카테고리
        @Query("y") y: String, // 기준점 위도
        @Query("x") x: String,  // 기준점 경도
        @Query("radius") radius: String,  // 범위
    ): Response<POIData>

    companion object {
        var retrofitService: POIRetrofitService? = null
        fun getInstance() : POIRetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(POI_ADDRESS)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(POIRetrofitService::class.java)
            }
            return retrofitService!!
        }
    }
}