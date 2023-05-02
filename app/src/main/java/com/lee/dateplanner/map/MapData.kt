package com.lee.dateplanner.map

import net.daum.mf.map.api.MapPOIItem.MarkerType

object MapData {
    data class MarkerItem(
        val hash: Int,
        val name: String,
        val mapPoint: Pair<Double, Double>,
        val markerType: MarkerType,
        val isKakaoRemove:Boolean = false
    )

    data class PolylineItem(
        val hash: Int,
        val color: Int,
        val startPoint: Pair<Double, Double>,
        val endPoint: Pair<Double, Double>
    )
}