package com.lee.dateplanner.festival.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lee.dateplanner.databinding.FestivalInfoRecyclerBinding
import com.lee.dateplanner.festival.data.FestivalInfoData

class FestivalRecyclerAdapter(private var festivalList: FestivalInfoData):RecyclerView.Adapter<FestivalViewHolder>() {
    private lateinit var binding: FestivalInfoRecyclerBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FestivalViewHolder {
        binding = FestivalInfoRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FestivalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FestivalViewHolder, position: Int) {
        val festival = festivalList.culturalEventInfo.row[position]
        with(holder.binding){
            festivalTitle.text = festival.tITLE
            festivalPlace.text = festival.pLACE
            festivalCost.text = festival.uSEFEE
            festivalDate.text = festival.dATE
            Glide.with(this.festivalPoster.context).load("""${festival.mAINIMG}""").into(this.festivalPoster)
        }
    }

    override fun getItemCount() = festivalList.culturalEventInfo.row.size
}