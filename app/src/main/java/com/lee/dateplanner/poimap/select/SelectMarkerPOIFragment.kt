package com.lee.dateplanner.poimap.select

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.jakewharton.rxbinding4.view.clicks
import com.lee.dateplanner.common.setCategoryTextFilter
import com.lee.dateplanner.databinding.SelectedMarkerPoiFragmentBinding
import com.lee.dateplanner.timetable.insert.InsertTimeSheetActivity
import com.lee.dateplanner.webview.WebViewActivity

/**
 * 선택한 marker 의 poi 정보를 보여주는 fragment
 */
class SelectMarkerPOIFragment:Fragment() {
    companion object{
        fun newInstance() = SelectMarkerPOIFragment()
    }
    private lateinit var binding: SelectedMarkerPoiFragmentBinding
    private lateinit var url: String
    private lateinit var placeName: String
    private lateinit var addressName: String
    private lateinit var phone: String
    private lateinit var latitude: String
    private lateinit var longitude: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SelectedMarkerPoiFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSelectPoi()
        listenerSetup()
        viewSetup()
    }

    // 뷰 셋팅 함수
    @SuppressLint("SetTextI18n")
    private fun viewSetup(){
        with(binding){
            parentFragmentManager.setFragmentResultListener("poiKey",viewLifecycleOwner) { _, bundle ->
                selectPoiName.text = bundle.getString("placeName").toString()
                selectPoiAddress.text = bundle.getString("addressName").toString()
                selectPoiPhone.text = bundle.getString("phone").toString()
                selectPoiCategory.text = setCategoryTextFilter(bundle.getString("category").toString())
                latitude = bundle.getString("latitude").toString()
                longitude = bundle.getString("longitude").toString()
                url = bundle.getString("placeUrl").toString()
            }
        }
    }
    // 버튼 세팅 함수
    private fun listenerSetup(){
        with(binding){
            selectPoiInsertBtn.clicks().subscribe {
                val intent = Intent(context, InsertTimeSheetActivity::class.java)
                intent.putExtra("input_signal","apiInput") // 일정 입력 창 종류
                intent.putExtra("title",selectPoiName.text)
                intent.putExtra("place",selectPoiAddress.text)
                intent.putExtra("latitude",latitude)
                intent.putExtra("longitude",longitude)
                context?.let { context -> ContextCompat.startActivity(context, intent, null) }
            }
            selectPoiWebViewBtn.clicks().subscribe {
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra("homepage",url)
                context?.let { it1 -> ContextCompat.startActivity(it1, intent, null) }
            }
        }
    }
    private fun getSelectPoi(){
        parentFragmentManager.setFragmentResultListener("poiKey",viewLifecycleOwner){ _, bundle ->
            placeName = bundle.getString("placeName").toString()
            addressName = bundle.getString("addressName").toString()
            phone = bundle.getString("phone").toString()
            url = bundle.getString("placeUrl").toString()
        }
    }
}