package com.lee.dateplanner.map.adpter

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lee.dateplanner.databinding.PoiListRecyclerBinding
import com.lee.dateplanner.map.AroundMapFragment
import com.lee.dateplanner.map.data.POIData
import net.daum.mf.map.api.CameraUpdateFactory
import net.daum.mf.map.api.MapView

class POIRecyclerAdapter(private val owner:AroundMapFragment, private val pois: POIData): RecyclerView.Adapter<POIViewHolder>() {
    private lateinit var binding: PoiListRecyclerBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): POIViewHolder {
        binding = PoiListRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return POIViewHolder(binding)
    }

    override fun onBindViewHolder(holder: POIViewHolder, position: Int) {
        val poi = pois.documents[position]
        with(holder.binding){
            poiCategory.text = poi.categoryName
            poiName.text = poi.placeName
            poiPhone.text = poi.phone
            poiAddress.text = poi.addressName

            // poi정보 리스트 터치시
            root.setOnClickListener {
                val marker = owner.markerResolver[poi]
                val update = CameraUpdateFactory.newMapPoint(marker!!.mapPoint, 2F) // 중심점, zoom 변경
                owner.binding.infoMap.animateCamera(update, object: net.daum.mf.map.api.CancelableCallback{
                    override fun onFinish() {
                        Log.e(TAG,"리스트 선택")
                        owner.binding.infoMap.selectPOIItem(marker,true) // 선택한 상점 마커 선택
                    }

                    override fun onCancel() {
                    }
                })
            }
        }
    }

    override fun getItemCount() = pois.documents.size

}