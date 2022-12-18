package com.lee.dateplanner.festival.adapter

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.location.Geocoder
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.RecyclerView
import com.lee.dateplanner.R
import com.lee.dateplanner.common.toastMessage
import com.lee.dateplanner.databinding.FestivalInfoRecyclerBinding
import com.lee.dateplanner.festival.FestivalListFragment
import com.lee.dateplanner.festival.data.FestivalListData
import com.lee.dateplanner.festival.data.FestivalSpaceData

/**
 * 행사 정보 출력 adapter
 */
class FestivalRecyclerAdapter(var fragment: FestivalListFragment, private var festivalData: MutableList<FestivalListData>, private val festivalSpaceData: FestivalSpaceData):RecyclerView.Adapter<FestivalViewHolder>() {
    private lateinit var binding: FestivalInfoRecyclerBinding
    var latitude = 0.0
    var longitude = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FestivalViewHolder {
        binding = FestivalInfoRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FestivalViewHolder(binding)
    }
    override fun onBindViewHolder(holder: FestivalViewHolder, position: Int) {
        val festival = festivalData[position]
        holder.setView(festival)
        holder.setListener(festival,this)
    }
    // 행사 장소 string 통해 위도, 경도 값 변환 함수
    fun getFestivalPosition(festival: FestivalListData) {
        val geoCoder = fragment.context?.let { Geocoder(it) } // 좌표변환 목적 geocoder 변수
        /**
         * 행사 장소 좌표 전달
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Deprecated 되지 않은 getFromLocationName 함수 활용 좌표 변환함수
            geoCoder?.getFromLocationName(festival.pLACE,1
            ) { addresses ->
                if(addresses.isNotEmpty()){
                    setPositionAndSendToPOIFragment(addresses[0].latitude, addresses[0].longitude)
                }
                else{ // geocoder 실패시 api 활용
                    getFestivalPositionFromFestivalPlaceData(festival)
                }
            }
        }else{ // 버전 안맞을시 Deprecated 함수사용
            val festivalPosition = geoCoder?.getFromLocationName(festival.pLACE,1)
            if (festivalPosition != null) {
                if (festivalPosition.isNotEmpty()){ // geocoder 변환 성공시
                    setPositionAndSendToPOIFragment(festivalPosition[0].latitude,festivalPosition[0].longitude)
                }
                else{ // geocoder 실패시 api 활용
                    getFestivalPositionFromFestivalPlaceData(festival)
                }
            }
        }
    }
    // festivalSpaceData 활용하여 장소 좌표 획득 함수
    private fun getFestivalPositionFromFestivalPlaceData(festival: FestivalListData){
        val searchPlace = festivalSpaceData.culturalSpaceInfo.row
        var placeCount = 0
        while (placeCount != searchPlace.size){
            if(searchPlace[placeCount].fACNAME.contains(festival.pLACE) || searchPlace[placeCount].fACDESC.contains(festival.pLACE)||
                festival.pLACE.contains(searchPlace[placeCount].fACNAME)){
                setPositionAndSendToPOIFragment(searchPlace[placeCount].xCOORD.toDouble(),searchPlace[placeCount].yCOORD.toDouble())
                this.latitude = searchPlace[placeCount].xCOORD.toDouble()
                this.longitude = searchPlace[placeCount].yCOORD.toDouble()
                break
            }
            placeCount ++
        }
        if(placeCount == searchPlace.size){
            toastMessage(fragment.getString(R.string.notFindFestivalLocation))
        }
    }
    // 좌표값 AroundMapFragment 로 보내기
    fun setPositionAndSendToPOIFragment(latitude: Double, longitude: Double){
        this.latitude = latitude
        this.longitude = longitude
        fragment.setFragmentResult("positionKey", bundleOf("latitude" to latitude, "longitude" to longitude))
        toastMessage(fragment.getString(R.string.findFestivalLocation))
    }
    @SuppressLint("NotifyDataSetChanged")
    fun refreshFestival(){
        notifyDataSetChanged()
    }
    override fun getItemCount() = festivalData.size
}