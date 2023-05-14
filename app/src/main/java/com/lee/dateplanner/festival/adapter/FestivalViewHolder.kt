package com.lee.dateplanner.festival.adapter

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding4.view.clicks
import com.lee.dateplanner.databinding.FestivalInfoRecyclerBinding
import com.lee.dateplanner.festival.data.FestivalInfoData
import com.lee.dateplanner.timetable.insert.InsertTimeSheetActivity
import com.lee.dateplanner.webview.WebViewActivity

class FestivalViewHolder(val binding: FestivalInfoRecyclerBinding): RecyclerView.ViewHolder(binding.root){
    fun setView(festival: FestivalInfoData.CulturalEventInfo.Row) = with(binding){
        festivalTitle.text = festival.tITLE
        festivalPlace.text = festival.pLACE
        festivalCost.text = festival.uSEFEE
        festivalDate.text = festival.dATE
        Glide.with(this.festivalPoster.context).load(festival.mAINIMG).into(this.festivalPoster)// 이미지 처리
    }
    fun setListener(festival: FestivalInfoData.CulturalEventInfo.Row, adapter: FestivalRecyclerAdapter){
        with(binding){
            //포스터 클릭 정의
            festivalPoster.clicks().subscribe{
                // 홈페이지 링크 전달 및 webView 수행 activity 이동
                val intent = Intent(itemView.context, WebViewActivity::class.java)
                intent.putExtra("homepage",festival.oRGLINK)
                ContextCompat.startActivity(itemView.context, intent, null)
            }
            // 일정 추가 버튼
            festivalInsertBtn.clicks().subscribe{
                adapter.getFestivalPosition(festival)
                val intent = Intent(itemView.context, InsertTimeSheetActivity::class.java)
                intent.putExtra("input_signal","apiInput") // 일정 입력 창 종류
                intent.putExtra("title",festival.tITLE)
                intent.putExtra("place",festival.pLACE)
                intent.putExtra("latitude",adapter.latitude.toString())
                intent.putExtra("longitude",adapter.longitude.toString())
                ContextCompat.startActivity(itemView.context, intent, null)
            }
            // 주변정보 보기 버튼
            festivalMovePoiBtn.clicks().subscribe{
                //aroundMap Fragment 로 좌표값, 행사장 정보 넘기기
                adapter.getFestivalPosition(festival)
            }
        }
    }
}