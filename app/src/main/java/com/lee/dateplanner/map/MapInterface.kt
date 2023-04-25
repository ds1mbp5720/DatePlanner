package com.lee.dateplanner.map

interface MapInterface {
    abstract fun addMarker(item: MapData.MarkerItem)

    abstract fun addMarker(item: MapData.MarkerItem, action: () -> (Unit))

    abstract fun removeMarker(hash: Int)

    abstract fun addPolyLine(item: MapData.PolylineItem)

    abstract fun addPolyLine(item: MapData.PolylineItem, action: () -> (Unit))

    abstract fun removePolyLine(hash: Int)

    abstract fun clear()
}