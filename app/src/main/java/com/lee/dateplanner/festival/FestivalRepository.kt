package com.lee.dateplanner.festival

import com.lee.dateplanner.festival.network.FestivalRetrofitService

class FestivalRepository(private val festivalRetrofitService: FestivalRetrofitService) {
    //행사 정보
    suspend fun getFestivalInfo() = festivalRetrofitService.getFestivalInfo()

    //행사 장소
    suspend fun getFestivalPlace() = festivalRetrofitService.getFestivalPlace()
}