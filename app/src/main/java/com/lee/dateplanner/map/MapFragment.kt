package com.lee.dateplanner.map

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.viewModels
import com.lee.dateplanner.R
import com.lee.dateplanner.base.BaseFragment
import com.lee.dateplanner.common.dpToPx
import com.lee.dateplanner.databinding.MapFragmentBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.util.CameraUtils
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class MapFragment: BaseFragment<MapFragmentBinding, MapViewModel>(), MapInterface {
    override val layoutId: Int = R.layout.map_fragment
    override val viewModel: MapViewModel by viewModels()

    private lateinit var mapController: MapController
    private val isNaver = true
    private var mapView: Any = Unit
    private var mapClickEvent: (() -> (Unit))? = null
    private var centerStart = Pair(0.0, 0.0)
    private var centerEnd = Pair(0.0, 0.0)
    private var centerLat = 0.0
    private var centerLng = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isNaver) {
            val mapFragment = MapFragment.newInstance().also { childFragmentManager.beginTransaction().add(R.id.map_view, it).commit() }
            mapFragment.getMapAsync {
                if (mapView !is NaverMap) mapView = it
                it.setOnMapClickListener { pointF, latLng ->
                    mapClickEvent?.invoke()
                }
                mapController = MapController(it).apply {
                    setCompass(dataBinding.compassNaver)
                }
                if (centerStart.first != 0.0 && centerEnd.first != 0.0) setCenterAtoB(centerStart, centerEnd)
                else if (centerLat != 0.0 && centerLng != 0.0) setMapCenterPoint(centerLat, centerLng)
            }
        }
    }

    fun setMapClickEvent(action: () -> Unit) {
        mapClickEvent = action
    }

    fun setCompassMargin(margin: Float) {
        if (this::mapController.isInitialized) {
            ConstraintSet().apply {
                clone(dataBinding.clMap)
                connect(
                    R.id.compass_naver,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP,
                    dpToPx(context ?: return@apply, margin)
                )
                applyTo(dataBinding.clMap)
            }
        }
    }
    fun setCompassPositionToStart(margin: Float){
        if (this::mapController.isInitialized) {
            ConstraintSet().apply {
                clone(dataBinding.clMap)
                clear(R.id.compass_naver, ConstraintSet.END)
                connect(
                    R.id.compass_naver,
                    ConstraintSet.START,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.START,
                    dpToPx(context ?: return@apply, margin/10)
                )
                connect(
                    R.id.compass_naver,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP,
                    dpToPx(context ?: return@apply, margin)
                )
                applyTo(dataBinding.clMap)
            }
        }
    }
    fun setCenterMarkers(data: List<Pair<Double, Double>>, zoom: Int = 1) {
        if (mapView is Unit) return
        if (isNaver) {
            (mapView as NaverMap).apply {
                val bounds = LatLngBounds.from(data.map { LatLng(it.first, it.second) })
                moveCamera(CameraUpdate.fitBounds(bounds))
                moveCamera(CameraUpdate.zoomTo(CameraUtils.getFittableZoom(this, bounds) - zoom))
            }
        } else {
            (mapView as MapView).apply {
                fitMapViewAreaToShowMapPoints(data.map { MapPoint.mapPointWithGeoCoord(it.first, it.second) }.toTypedArray())
                postDelayed({
                    setZoomLevel(zoomLevel + zoom - 1, false)
                }, 500)
            }
        }
    }

    fun setMapCenterPoint(lat: Double, lng: Double, zoom: Int? = null) {
        centerLat = lat
        centerLng = lng
        if (mapView is Unit) return
        if (isNaver) {
            (mapView as NaverMap).apply {
                moveCamera(CameraUpdate.scrollTo(LatLng(lat, lng)))
            }
        } else {
            if ((mapView is MapView)) {
                (mapView as MapView).apply {
                    if (zoom != null) setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(lat, lng), zoom, true)
                    else setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lat, lng), true)
                }
            }
        }
    }

    fun setCenterAtoB(start: Pair<Double, Double>, end: Pair<Double, Double>) {
        centerStart = start
        centerEnd = end
        if (mapView is Unit) return
        if (isNaver) {
            (mapView as NaverMap).apply {
                val bounds = LatLngBounds.from(listOf(LatLng(start.first, start.second), LatLng(end.first, end.second)))
                moveCamera(CameraUpdate.fitBounds(bounds))
                moveCamera(CameraUpdate.zoomTo(CameraUtils.getFittableZoom(this, bounds) - 1))
            }
        } else {
            if (mapView is MapView) {
                (mapView as MapView).apply {
                    val latitude = kotlin.math.abs(start.first - end.first)
                    val longitude = kotlin.math.abs(start.second - end.second)
                    if (latitude > longitude) {                   ////위도값이 더 큰 지도 --> 들어온 위도값중 더 큰 값에다가 총 거리의 1/7 의 거리값을 더하여 패딩을 넣고, 들어온 위도값 중 더 작은 값에다가 총 거리의 1/7의 거리값을 빼서 패딩을 넣는다, 경도도 마찬가지
                        if (start.first > end.first) {
                            fitMapViewAreaToShowMapPoints(
                                arrayOf(
                                    MapPoint.mapPointWithGeoCoord(start.first + latitude / 7, start.second),
                                    MapPoint.mapPointWithGeoCoord(end.first - latitude / 7, end.second)
                                )
                            )
                        } else {
                            fitMapViewAreaToShowMapPoints(
                                arrayOf(
                                    MapPoint.mapPointWithGeoCoord(start.first - latitude / 7, start.second),
                                    MapPoint.mapPointWithGeoCoord(end.first + latitude / 7, end.second)
                                )
                            )
                        }
                    } else {                                      ////경도값이 더 큰 지도
                        if (start.second > end.second) {
                            fitMapViewAreaToShowMapPoints(
                                arrayOf(
                                    MapPoint.mapPointWithGeoCoord(start.first, start.second + longitude / 7),
                                    MapPoint.mapPointWithGeoCoord(end.first, end.second - longitude / 7)
                                )
                            )
                        } else {
                            fitMapViewAreaToShowMapPoints(
                                arrayOf(
                                    MapPoint.mapPointWithGeoCoord(start.first, start.second - longitude / 7),
                                    MapPoint.mapPointWithGeoCoord(end.first, end.second + longitude / 7)
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun addMarkerAtoB(start: MapData.MarkerItem, end: MapData.MarkerItem, line: MapData.PolylineItem) {
        addMarker(start)
        addMarker(end)
        addPolyLine(line)
    }

    override fun clear() {
        if (this::mapController.isInitialized)
            mapController.clear()
    }

    override fun addMarker(item: MapData.MarkerItem) {
        if (this::mapController.isInitialized)
            mapController.addMarker(item)
    }

    override fun addMarker(item: MapData.MarkerItem, action: () -> (Unit)) {
        if (this::mapController.isInitialized)
            mapController.addMarker(item, action)
    }

    override fun removeMarker(hash: Int) {
        if (this::mapController.isInitialized)
            mapController.removeMarker(hash)
    }

    override fun addPolyLine(item: MapData.PolylineItem, action: () -> (Unit)) {
        if (this::mapController.isInitialized)
            mapController.addPolyLine(item, action)
    }

    override fun addPolyLine(item: MapData.PolylineItem) {
        if (this::mapController.isInitialized)
            mapController.addPolyLine(item)
    }

    override fun removePolyLine(hash: Int) {
        if (this::mapController.isInitialized)
            mapController.removePolyLine(hash)
    }

    override fun onDestroy() {
        super.onDestroy()
        clear()
    }

    companion object {
        enum class MapType {
            KAKAO, NAVER
        }
    }

}