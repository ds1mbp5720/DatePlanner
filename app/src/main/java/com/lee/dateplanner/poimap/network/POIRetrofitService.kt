package com.lee.dateplanner.poimap.network


import com.lee.dateplanner.BuildConfig
import com.lee.dateplanner.poimap.data.POIData
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

const val POI_ADDRESS = "https://dapi.kakao.com/"

interface POIRetrofitService {

    @Headers("Authorization:KakaoAK ${BuildConfig.KAKAO_REST_KEY}")
    @GET("v2/local/search/category.json") // poi 정보 api
    suspend fun getPOIInfo(
        @Query("category_group_code") category_group_code: String, // poi 종류 카테고리
        @Query("y") y: String, // 기준점 위도
        @Query("x") x: String,  // 기준점 경도
        @Query("radius") radius: String,  // 범위
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String
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