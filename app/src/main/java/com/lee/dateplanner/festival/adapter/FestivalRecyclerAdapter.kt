package com.lee.dateplanner.festival.adapter

import android.content.ContentValues.TAG
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lee.dateplanner.R
import com.lee.dateplanner.common.toastMessage
import com.lee.dateplanner.databinding.FestivalInfoRecyclerBinding
import com.lee.dateplanner.festival.FestivalListFragment
import com.lee.dateplanner.festival.webview.WebViewFestivalActivity
import com.lee.dateplanner.festival.data.FestivalInfoData
import com.lee.dateplanner.festival.data.FestivalSpaceData
import com.lee.dateplanner.map.POIMapFragment
import com.lee.dateplanner.timetable.insert.InsertTimeSheetActivity

/**
 * 행사 정보 출력 adapter
 */
class FestivalRecyclerAdapter(var fragment: FestivalListFragment, private var festivalList: FestivalInfoData, private var festivalPlace: FestivalSpaceData):RecyclerView.Adapter<FestivalViewHolder>() {
    private lateinit var binding: FestivalInfoRecyclerBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FestivalViewHolder {
        binding = FestivalInfoRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FestivalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FestivalViewHolder, position: Int) {
        var festival = festivalList.culturalEventInfo.row[position]
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
                intent.putExtra("input_signal","festival") // 일정 입력 창 종류
                intent.putExtra("festivalTitle",festival.tITLE)
                intent.putExtra("festivalPlace",festival.pLACE)
                startActivity(holder.itemView.context, intent,null)
            }
            // 주변정보 보기 버튼
            festivalMovePoiBtn.setOnClickListener {
                //aroundMap Fragment 로 좌표값, 행사장 정보 넘기기
                getFestivalPosition(festival)
                sendPositionToOtherFragment()
            }
        }
    }
    private var latitude = 0.0 // 위도 저장 변수
    private var longitude = 0.0 // 경도 저장 변수
    // 행사 장소 string을 통해 위도, 경도 값 변환 함수
    private fun getFestivalPosition(festival: FestivalInfoData.CulturalEventInfo.Row) {
        var geoCoder = fragment.context?.let { Geocoder(it) } // 좌표변환 목적 geocoder 변수
        /**
         * 행사 장소 좌표 전달
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Deprecated 되지 않은 getFromLocationName 함수 활용 좌표 변환함수
            geoCoder?.getFromLocationName(festival.pLACE,1,object: GeocodeListener{
                override fun onGeocode(addresses: MutableList<Address>) {
                    if(addresses != null){
                        Log.e(TAG,"geo 결과: ${addresses[0].latitude} , ${addresses[0].longitude}")
                        latitude = addresses[0].latitude
                        longitude = addresses[0].longitude
                    }
                    else{ // geocoder 실패시 api 활용
                        getFestivalPositionFromFestivalPlaceData(festival)
                    }
                }
            })
        }else{ // 버전 안맞을시 Deprecated 함수사용
            var festivalPosition = geoCoder?.getFromLocationName(festival.pLACE,1)
            if (festivalPosition != null) {
                if (festivalPosition.isNotEmpty()){ // geocoder 변환 성공시
                    Log.e(TAG,"geo 구버전 결과: ${festivalPosition[0].latitude} , ${festivalPosition[0].longitude}")
                    latitude = festivalPosition[0].latitude
                    longitude = festivalPosition[0].longitude
                }
                else{ // geocoder 실패시 api 활용
                    getFestivalPositionFromFestivalPlaceData(festival)
                }
            }
        }
    }
    // festivalSpaceData 활용하여 장소 좌표 획득 함수
    private fun getFestivalPositionFromFestivalPlaceData(festival: FestivalInfoData.CulturalEventInfo.Row){
        var searchPlace = festivalPlace.culturalSpaceInfo.row
        var placeCount = 0
        while (placeCount != searchPlace.size){
            if(searchPlace[placeCount].fACNAME.contains(festival.pLACE) || searchPlace[placeCount].fACDESC.contains(festival.pLACE)||
                festival.pLACE.contains(searchPlace[placeCount].fACNAME)){
                Log.e(TAG,"장소 api 활용: ${searchPlace[placeCount].fACNAME} 좌표: ${searchPlace[placeCount].xCOORD} ${searchPlace[placeCount].yCOORD}")
                latitude = searchPlace[placeCount].xCOORD.toDouble()
                longitude = searchPlace[placeCount].yCOORD.toDouble()
                break
            }
            placeCount ++
        }
        if(placeCount == searchPlace.size){
            toastMessage("장소가 등록되지 않은 행사입니다.")
        }
    }
    // 좌표값 AroundMapFragment 로 보내기
    private fun sendPositionToOtherFragment(){
        fragment.setFragmentResult("latitudeKey", bundleOf("latitude" to latitude))
        fragment.setFragmentResult("longitudeKey", bundleOf("longitude" to longitude))
    }
    override fun getItemCount() = festivalList.culturalEventInfo.row.size
}