package com.lee.dateplanner.festival.adapter

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lee.dateplanner.MainActivity
import com.lee.dateplanner.databinding.FestivalInfoRecyclerBinding
import com.lee.dateplanner.festival.WebViewFestivalActivity
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

            //포스터 클릭 정의
            festivalPoster.setOnClickListener{
                // 홈페이지 링크 전달 및 activity 이동
                val intent = Intent(holder.itemView.context,WebViewFestivalActivity::class.java)
                intent.putExtra("homepage",festival.oRGLINK)
                startActivity(holder.itemView.context, intent,null)
            }
        }
    }

    override fun getItemCount() = festivalList.culturalEventInfo.row.size
}