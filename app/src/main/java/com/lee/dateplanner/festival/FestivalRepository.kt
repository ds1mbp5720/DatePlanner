package com.lee.dateplanner.festival

import com.lee.dateplanner.festival.network.FestivalRetrofitService
import javax.inject.Inject

class FestivalRepository @Inject constructor(private val festivalRetrofitService: FestivalRetrofitService) {
    //행사 정보
    suspend fun getFestivalInfo(category: String) = festivalRetrofitService.getFestivalInfo(category)

    //행사 장소
    suspend fun getFestivalPlace() = festivalRetrofitService.getFestivalPlace()
}