package com.lee.dateplanner.map.adpter

import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.jakewharton.rxbinding4.view.clicks
import com.lee.dateplanner.common.setCategoryTextFilter
import com.lee.dateplanner.databinding.PoiListRecyclerBinding
import com.lee.dateplanner.map.POIMapFragment
import com.lee.dateplanner.map.data.POIData

class POIViewHolder(val binding: PoiListRecyclerBinding, owner: POIMapFragment): RecyclerView.ViewHolder(binding.root){
    private val behavior = BottomSheetBehavior.from(owner.dataBinding.bottomPoiList)
    fun setView(poi : POIData.Document) = with(binding){
        poiCategory.text = setCategoryTextFilter(poi.categoryName)
        poiName.text = poi.placeName
        poiPhone.text = poi.phone
        poiAddress.text = poi.addressName
    }
    fun setListener(poi : POIData.Document, adapter: POIRecyclerAdapter){
        // poi 정보 리스트중 1개 터치시 발생 이벤트
        binding.root.clicks().subscribe {
            adapter.moveToMarker(poi)
            adapter.sendSelectRecyclerInfo(poi)
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }
}