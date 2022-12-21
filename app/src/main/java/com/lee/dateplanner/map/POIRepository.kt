package com.lee.dateplanner.map

import com.lee.dateplanner.map.network.POIRetrofitService

class POIRepository(private val poiRetrofitService: POIRetrofitService) {
    // poi정보 카테고리, lat, lgt 추가하기, 반경 3km 설정
    suspend fun getPOIInfo(category: String, lat:String, lgt: String) = poiRetrofitService.getPOIInfo(category,lat,lgt,"2000")
}