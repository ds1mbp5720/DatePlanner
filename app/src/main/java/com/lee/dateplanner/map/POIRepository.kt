package com.lee.dateplanner.map

import com.lee.dateplanner.map.network.POIRetrofitService

class POIRepository(private val poiRetrofitService: POIRetrofitService) {
    // poi 카테고리, lat, lgt, 반경 2km 설정, 페이징, 크기, 정렬 양식
    suspend fun getPOIInfo(category: String, lat:String, lgt: String, page: Int, size: Int)
        = poiRetrofitService.getPOIInfo(category,lat,lgt,"2000",page,size,"accuracy")
}