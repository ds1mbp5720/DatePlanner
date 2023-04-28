package com.lee.dateplanner.poimap.adpter

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.lee.dateplanner.databinding.PoiListRecyclerBinding
import com.lee.dateplanner.festival.data.FestivalInfoData
import com.lee.dateplanner.poimap.POIMapFragment
import com.lee.dateplanner.poimap.data.POIData
import kotlinx.coroutines.*
import net.daum.mf.map.api.CameraUpdateFactory

class POIRecyclerAdapter(private val owner:POIMapFragment): RecyclerView.Adapter<POIViewHolder>() {
    private lateinit var binding: PoiListRecyclerBinding
    private val poiData = mutableListOf<POIData.Document>()
    var job : Job? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): POIViewHolder {
        binding = PoiListRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return POIViewHolder(binding,owner)
    }
    fun setPoiItem(item : List<POIData.Document>){
        poiData.clear()
        poiData.addAll(item)
    }
    override fun onBindViewHolder(holder: POIViewHolder, position: Int) {
        val poi = poiData[position]
        holder.setView(poi)
        holder.setListener(poi,this)
    }
    override fun getItemCount() = poiData.size

    // 리스트에서 poi 선택시 정보 전달 및 selectMarkerPOIFragment 호출 함수
    fun sendSelectRecyclerInfo(poi: POIData.Document){
        with(poi){
            job = CoroutineScope(Dispatchers.Main).launch(exceptionHandler) {
                owner.childFragmentManager.setFragmentResult("poiKey", bundleOf("placeName" to placeName, "addressName" to addressName,
                    "phone" to phone, "category" to categoryName, "placeUrl" to placeUrl, "latitude" to y, "longitude" to x)
                )
                owner.childFragmentManager.beginTransaction()
                    .replace(com.lee.dateplanner.R.id.selected_marker_info,
                        owner.selectMarkerPOIFragment
                    ).addToBackStack(null).commit()
            }
        }
    }
    // 마커로 지도 중심 이동 함수
    fun moveToMarker(poi: POIData.Document){
        val marker = owner.markerResolver[poi]
        // 해당 위치로 지도 중심점 이동, 지도 확대
        if(marker != null){
            val update = CameraUpdateFactory.newMapPoint(marker.mapPoint, 2F)
            with(owner.mapView){
                animateCamera(update, object: net.daum.mf.map.api.CancelableCallback{
                    override fun onFinish() {
                        selectPOIItem(marker,true) // 선택한 상점 마커 선택
                        refreshMapTiles()
                    }
                    override fun onCancel() {}
                })
            }
        }else{
            Log.e(TAG,"마커 null")
        }
    }

    val errorMessage = MutableLiveData<String>()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("코루틴내 예외: ${throwable.localizedMessage}")
    }
    private fun onError(message: String){
        errorMessage.postValue(message)
    }
}