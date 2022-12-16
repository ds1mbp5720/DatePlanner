package com.lee.dateplanner.festival.adapter

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.RecyclerView
import com.lee.dateplanner.common.toastMessage
import com.lee.dateplanner.databinding.FestivalInfoRecyclerBinding
import com.lee.dateplanner.festival.FestivalListFragment
import com.lee.dateplanner.festival.data.FestivalInfoData
import com.lee.dateplanner.festival.data.FestivalSpaceData

/**
 * 행사 정보 출력 adapter
 */
class FestivalRecyclerAdapter(var fragment: FestivalListFragment, private var festivalData: FestivalInfoData, private var festivalPlace: FestivalSpaceData):RecyclerView.Adapter<FestivalViewHolder>() {
    private lateinit var binding: FestivalInfoRecyclerBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FestivalViewHolder {
        binding = FestivalInfoRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FestivalViewHolder(binding)
    }
    override fun onBindViewHolder(holder: FestivalViewHolder, position: Int) {
        val festival = festivalData.culturalEventInfo.row[position]
        holder.setView(festival, position)
        holder.setListener(festival,this)
    }
    var latitude = 0.0 // 위도 저장 변수
    var longitude = 0.0 // 경도 저장 변수
    // 행사 장소 string을 통해 위도, 경도 값 변환 함수
    fun getFestivalPosition(festival: FestivalInfoData.CulturalEventInfo.Row) {
        val geoCoder = fragment.context?.let { Geocoder(it) } // 좌표변환 목적 geocoder 변수
        /**
         * 행사 장소 좌표 전달
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Deprecated 되지 않은 getFromLocationName 함수 활용 좌표 변환함수
            geoCoder?.getFromLocationName(festival.pLACE,1,object: GeocodeListener{
                override fun onGeocode(addresses: MutableList<Address>) {
                    latitude = addresses[0].latitude
                    longitude = addresses[0].longitude
                    toastMessage("행사 장소 주변 정보를 얻었습니다.")
                }
            })
        }else{ // 버전 안맞을시 Deprecated 함수사용
            val festivalPosition = geoCoder?.getFromLocationName(festival.pLACE,1)
            if (festivalPosition != null) {
                if (festivalPosition.isNotEmpty()){ // geocoder 변환 성공시
                    latitude = festivalPosition[0].latitude
                    longitude = festivalPosition[0].longitude
                    toastMessage("행사 장소 주변 정보를 얻었습니다.")
                }
                else{ // geocoder 실패시 api 활용
                    getFestivalPositionFromFestivalPlaceData(festival)
                }
            }
        }
    }
    // festivalSpaceData 활용하여 장소 좌표 획득 함수
    private fun getFestivalPositionFromFestivalPlaceData(festival: FestivalInfoData.CulturalEventInfo.Row){
        val searchPlace = festivalPlace.culturalSpaceInfo.row
        var placeCount = 0
        while (placeCount != searchPlace.size){
            if(searchPlace[placeCount].fACNAME.contains(festival.pLACE) || searchPlace[placeCount].fACDESC.contains(festival.pLACE)||
                festival.pLACE.contains(searchPlace[placeCount].fACNAME)){
                latitude = searchPlace[placeCount].xCOORD.toDouble()
                longitude = searchPlace[placeCount].yCOORD.toDouble()
                toastMessage("행사 장소 주변 정보를 얻었습니다.")
                break
            }
            placeCount ++
        }
        if(placeCount == searchPlace.size){
            toastMessage("장소가 등록되지 않은 행사입니다.")
        }
    }
    // 좌표값 AroundMapFragment 로 보내기
    fun sendPositionToOtherFragment(){
        fragment.setFragmentResult("positionKey", bundleOf("latitude" to latitude, "longitude" to longitude))
    }
    @SuppressLint("NotifyDataSetChanged")
    fun refreshFestival(){
        notifyDataSetChanged()
    }
    override fun getItemCount() = festivalData.culturalEventInfo.row.size
}