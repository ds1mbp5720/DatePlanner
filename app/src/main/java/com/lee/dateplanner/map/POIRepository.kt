package com.lee.dateplanner.map

import com.lee.dateplanner.map.network.POIRetrofitService

class POIRepository(private val poiRetrofitService: POIRetrofitService) {
    // poi정보 카테고리, lat, lgt 추가하기
    suspend fun getPOIInfo(category: String, lat:String, lgt: String) = poiRetrofitService.getPOIInfo(category,lat,lgt,"3000")
}