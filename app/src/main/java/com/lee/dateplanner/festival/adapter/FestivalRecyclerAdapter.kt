package com.lee.dateplanner.festival.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lee.dateplanner.R
import com.lee.dateplanner.databinding.FestivalInfoRecyclerBinding
import com.lee.dateplanner.festival.webview.WebViewFestivalActivity
import com.lee.dateplanner.festival.data.FestivalInfoData
import com.lee.dateplanner.timetable.insert.InsertTimeSheetActivity

/**
 * 행사 정보 출력 adapter
 */
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
            Glide.with(this.festivalPoster.context).load("""${festival.mAINIMG}""").into(this.festivalPoster)// 이미지 처리

            //포스터 클릭 정의
            festivalPoster.setOnClickListener{
                // 홈페이지 링크 전달 및 webView 수행 activity 이동
                val intent = Intent(holder.itemView.context, WebViewFestivalActivity::class.java)
                intent.putExtra("homepage",festival.oRGLINK)
                startActivity(holder.itemView.context, intent,null)
            }
            // 일정 추가 버튼
            festivalInsertBtn.setOnClickListener {
                val intent = Intent(holder.itemView.context, InsertTimeSheetActivity::class.java)
                intent.putExtra(R.string.input_signal.toString(),"festival") // 일정 입력 창 종류
                intent.putExtra("festivalTitle",festival.tITLE)
                intent.putExtra("festivalPlace",festival.pLACE)
                startActivity(holder.itemView.context, intent,null)
            }
            // 주변정보 보기 버튼
            festivalMovePoiBtn.setOnClickListener {
                /**
                 * 행사 장소 좌표 전달
                 */
            }
        }
    }

    override fun getItemCount() = festivalList.culturalEventInfo.row.size
}