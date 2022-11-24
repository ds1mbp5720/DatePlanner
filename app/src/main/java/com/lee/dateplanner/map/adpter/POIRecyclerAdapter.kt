package com.lee.dateplanner.map.adpter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lee.dateplanner.databinding.PoiListRecyclerBinding
import com.lee.dateplanner.map.data.POIData

class POIRecyclerAdapter(private val pois: POIData): RecyclerView.Adapter<POIViewHolder>() {
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
        }


    }

    override fun getItemCount() = pois.documents.size

}