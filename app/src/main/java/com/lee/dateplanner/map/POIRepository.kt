package com.lee.dateplanner.map

import com.lee.dateplanner.map.network.POIRetrofitService

class POIRepository(private val poiRetrofitService: POIRetrofitService) {
    suspend fun getPOIInfo() = poiRetrofitService.getPOIInfo()
}