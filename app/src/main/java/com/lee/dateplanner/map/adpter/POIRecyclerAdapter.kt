package com.lee.dateplanner.map.adpter

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lee.dateplanner.databinding.PoiListRecyclerBinding
import com.lee.dateplanner.map.POIMapFragment
import com.lee.dateplanner.map.SelectMarkerPOIFragment
import com.lee.dateplanner.map.data.POIData
import kotlinx.coroutines.*
import net.daum.mf.map.api.CameraUpdateFactory

class POIRecyclerAdapter(private val owner:POIMapFragment, private val pois: POIData): RecyclerView.Adapter<POIViewHolder>() {
    private lateinit var binding: PoiListRecyclerBinding
    var job : Job? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): POIViewHolder {
        binding = PoiListRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return POIViewHolder(binding)
    }

    override fun onBindViewHolder(holder: POIViewHolder, position: Int) {
        val poi = pois.documents[position]
        val behavior = BottomSheetBehavior.from(owner.binding.bottomPoiList)
        with(holder.binding){
            poiCategory.text = poi.categoryName
            poiName.text = poi.placeName
            poiPhone.text = poi.phone
            poiAddress.text = poi.addressName

            // poi정보 리스트 터치시
            root.setOnClickListener {
                moveToMarker(poi)
                sendSelectRecyclerInfo(poi)
                owner.binding.infoMap.refreshMapTiles()
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }
    override fun getItemCount() = pois.documents.size

    // 리스트에서 poi 선택시 정보 전달 및 selectMarkerPOIFragment 호출 함수
    private fun sendSelectRecyclerInfo(poi: POIData.Document){
        with(poi){
            job = CoroutineScope(Dispatchers.Main).launch(exceptionHandler) {
                owner.childFragmentManager.setFragmentResult("poiKey", bundleOf("placeName" to placeName, "addressName" to addressName,
                    "phone" to phone, "distance" to distance, "placeUrl" to placeUrl)
                )
                owner.childFragmentManager.beginTransaction()
                    .replace(com.lee.dateplanner.R.id.selected_marker_info,
                        owner.selectMarkerPOIFragment
                    ).addToBackStack(null).commit()
            }
        }
    }
    // 마커로 지도 중심 이동 함수
    private fun moveToMarker(poi: POIData.Document){
        val marker = owner.markerResolver[poi]
        // 해당 위치로 지도 중심점 이동, 지도 확대
        if(marker != null){
            val update = CameraUpdateFactory.newMapPoint(marker?.mapPoint, 2F)
            with(owner.binding){
                infoMap.animateCamera(update, object: net.daum.mf.map.api.CancelableCallback{
                    override fun onFinish() {
                        owner.binding.infoMap.selectPOIItem(marker,true) // 선택한 상점 마커 선택
                    }
                    override fun onCancel() {
                    }
                })
                infoMap.refreshMapTiles()
            }
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