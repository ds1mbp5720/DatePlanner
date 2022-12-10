package com.lee.dateplanner.festival.network

import com.lee.dateplanner.BuildConfig
import com.lee.dateplanner.festival.data.FestivalInfoData
import com.lee.dateplanner.festival.data.FestivalSpaceData
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

const val FESTIVAL_ADDRESS = "http://openapi.seoul.go.kr:8088/${BuildConfig.FESTIVAL_KEY}/json/"

interface FestivalRetrofitService {
    @GET("culturalEventInfo/1/1000/{CODENAME}") // 행사 정보 api
    // 카테고리 문화교양/강좌,전시/미술,뮤지컬/오페라,기타,연극,무용,영화,
    // 국악,콘서트,축제-문화/예술,축제-전통/역사,축제-시민화합,클래식,축제-기타,축제-자연/경관,독주/독창회
    suspend fun getFestivalInfo(
        @Path("CODENAME") CODENAME:String
    ): Response<FestivalInfoData>
    @GET("culturalSpaceInfo/1/819/") // 행사 장소 정보 api
    suspend fun getFestivalPlace(): Response<FestivalSpaceData>

    companion object {
        private var retrofitService: FestivalRetrofitService? = null
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