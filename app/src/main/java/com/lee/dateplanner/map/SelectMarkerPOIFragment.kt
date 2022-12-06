package com.lee.dateplanner.map

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lee.dateplanner.databinding.SelectedMarkerPoiFragmentBinding

/**
 * 선택한 marker 의 poi 정보를 보여주는 fragment
 */
class SelectMarkerPOIFragment:Fragment() {
    companion object{
        fun newInstance() = POIMapFragment()
    }
    private lateinit var binding: SelectedMarkerPoiFragmentBinding
    private lateinit var url: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SelectedMarkerPoiFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenerSetup()
        viewSetup()
    }

    // 뷰 셋팅 함수
    private fun viewSetup(){
        with(binding){
            parentFragmentManager.setFragmentResultListener("poiKey",viewLifecycleOwner){ _, bundle ->
                selectPoiName.text= bundle.getString("placeName").toString()
                selectPoiAddress.text = bundle.getString("addressName").toString()
                selectPoiPhone.text = bundle.getString("phone").toString()
                selectPoiDistance.text = bundle.getString("distance").toString()
                url = bundle.getString("placeUrl").toString()
                Log.e(TAG,"넘어온 값 ${bundle.getString("phone")}")
            }
        }
    }
    // 버튼 세팅 함수
    private fun listenerSetup(){
        with(binding){
            selectPoiInsertBtn.setOnClickListener {
                Log.e(TAG,"일정추가 버튼")
            }
            selectPoiWebViewBtn.setOnClickListener {
                Log.e(TAG,"webView 버튼")
            }
        }

    }
}