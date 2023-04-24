package com.lee.dateplanner.festival

import com.lee.dateplanner.festival.apimodule.ApiModule
import com.lee.dateplanner.festival.network.FestivalRetrofitService
import javax.inject.Inject

class FestivalRepository @Inject constructor(@ApiModule.TypeFestival private val festivalRetrofitService: FestivalRetrofitService) {
    //행사 정보
    suspend fun getFestivalInfo(category: String) = festivalRetrofitService.getFestivalInfo(category)

    suspend fun getPagingFestivalInfo(category: String, start: Int, end: Int) = festivalRetrofitService.getPagingFestivalInfo(start,end,category)

    //행사 장소
    suspend fun getFestivalPlace() = festivalRetrofitService.getFestivalPlace()
}