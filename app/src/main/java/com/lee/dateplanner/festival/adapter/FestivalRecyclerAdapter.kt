package com.lee.dateplanner.festival.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.lee.dateplanner.R
import com.lee.dateplanner.common.FestivalInfoEventBus
import com.lee.dateplanner.databinding.FestivalInfoRecyclerBinding
import com.lee.dateplanner.festival.FestivalListFragment
import com.lee.dateplanner.festival.FestivalViewModel
import com.lee.dateplanner.festival.data.FestivalInfoData
import com.lee.dateplanner.festival.data.FestivalSpaceData
import com.lee.dateplanner.timetable.insert.InsertTimeSheetActivity
import org.greenrobot.eventbus.EventBus

/**
 * 행사 정보 출력 adapter
 */
class FestivalRecyclerAdapter(var fragment: FestivalListFragment):
    RecyclerView.Adapter<FestivalViewHolder>() {
    private lateinit var binding: FestivalInfoRecyclerBinding
    private val festivalData = mutableListOf<FestivalInfoData.CulturalEventInfo.Row>()
    private val festivalSpaceData = mutableListOf<FestivalSpaceData.CulturalSpaceInfo.Row>()
    private lateinit var viewModel: FestivalViewModel

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
    fun setFestivalData(item: MutableList<FestivalInfoData.CulturalEventInfo.Row>){
        festivalData.clear()
        festivalData.addAll(item)
        notifyDataSetChanged()
    }
    fun setFestivalSpaceData(item: MutableList<FestivalSpaceData.CulturalSpaceInfo.Row>){
        festivalSpaceData.clear()
        festivalSpaceData.addAll(item)
    }
    fun setViewModel(festivalViewModel: FestivalViewModel){
        viewModel = festivalViewModel
    }
    // 행사 장소 string 통해 위도, 경도 값 변환 함수
    fun getFestivalPosition(festival: FestivalInfoData.CulturalEventInfo.Row, useEventBus: Boolean) {
        val geoCoder = fragment.context?.let { Geocoder(it) } // 좌표변환 목적 geocoder 변수
        //행사 장소 좌표 전달
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Deprecated 되지 않은 getFromLocationName 함수 활용 좌표 변환함수
            geoCoder?.getFromLocationName(festival.pLACE,1
            ) { addresses ->
                if(addresses.isNotEmpty()){
                    setPositionAndSendToPOIFragment(festival,addresses[0].latitude, addresses[0].longitude,useEventBus)
                }
                else{ // geocoder 실패시 api 활용
                    getFestivalPositionFromFestivalPlaceData(festival,useEventBus)
                }
            }
        }else{ // 버전 안맞을시 Deprecated 함수사용
            val festivalPosition = geoCoder?.getFromLocationName(festival.pLACE,1)
            if (festivalPosition != null) {
                if (festivalPosition.isNotEmpty()){ // geocoder 변환 성공시
                    setPositionAndSendToPOIFragment(festival,festivalPosition[0].latitude,festivalPosition[0].longitude,useEventBus)
                }
                else{ // geocoder 실패시 api 활용
                    getFestivalPositionFromFestivalPlaceData(festival,useEventBus)
                }
            }
        }
    }
    // festivalSpaceData 활용하여 장소 좌표 획득 함수
    private fun getFestivalPositionFromFestivalPlaceData(festival: FestivalInfoData.CulturalEventInfo.Row, useEventBus: Boolean){
        val searchPlace = festivalSpaceData
        var placeCount = 0
        while (placeCount != searchPlace.size){
            if(searchPlace[placeCount].fACNAME.contains(festival.pLACE) || searchPlace[placeCount].fACDESC.contains(festival.pLACE)||
                festival.pLACE.contains(searchPlace[placeCount].fACNAME)){
                setPositionAndSendToPOIFragment(festival,searchPlace[placeCount].xCOORD.toDouble(),searchPlace[placeCount].yCOORD.toDouble(), useEventBus)
                this.latitude = searchPlace[placeCount].xCOORD.toDouble()
                this.longitude = searchPlace[placeCount].yCOORD.toDouble()
                break
            }
            placeCount ++
        }
        if(placeCount == searchPlace.size){
            viewModel.onGetLocationCheck(false)
        }
    }
    // 좌표값 POIMapFragment 로 보내기
    fun setPositionAndSendToPOIFragment(festival: FestivalInfoData.CulturalEventInfo.Row, latitude: Double, longitude: Double, useEventBus: Boolean){
        this.latitude = latitude
        this.longitude = longitude
        viewModel.onGetLocationCheck(true)
        if(useEventBus)
            sendToEventBus()
        else
            sentToInsertActivity(festival)
    }
    private fun sendToEventBus(){
        EventBus.getDefault().post(FestivalInfoEventBus(latitude, longitude))
    }
    private fun sentToInsertActivity(festival: FestivalInfoData.CulturalEventInfo.Row){
        val intent = Intent(binding.root.context, InsertTimeSheetActivity::class.java)
        intent.putExtra("input_signal","apiInput") // 일정 입력 창 종류
        intent.putExtra("title",festival.tITLE)
        intent.putExtra("place",festival.pLACE)
        intent.putExtra("latitude",latitude.toString())
        intent.putExtra("longitude",longitude.toString())
        startActivity(binding.root.context, intent, null)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun refreshFestival(){
        notifyDataSetChanged()
    }
    override fun getItemCount() = festivalData.size
}