package com.lee.dateplanner.festival

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lee.dateplanner.R
import com.lee.dateplanner.common.dateStringFormat
import com.lee.dateplanner.common.makeDatePickerDialog
import com.lee.dateplanner.databinding.FestivallistFragmentLayoutBinding
import com.lee.dateplanner.festival.adapter.FestivalRecyclerAdapter
import com.lee.dateplanner.festival.network.FestivalRetrofitService
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

class FestivalListFragment:Fragment() {
    companion object{
        fun newInstance() = FestivalListFragment()
    }
    private lateinit var binding: FestivallistFragmentLayoutBinding
    private lateinit var viewModel : FestivalViewModel
    private var category = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FestivallistFragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, FestivalViewModelFactory(
            FestivalRepository(FestivalRetrofitService.getInstance()))
        )[FestivalViewModel::class.java]

        listenerSetup()
        // 행사 정보 처리
        viewModel.festivalList.observe(viewLifecycleOwner){
            val festivalList = it
            // 행사 장소 정보 처리
            viewModel.festivalPlaceList.observe(viewLifecycleOwner){
                with(binding.festivalList){
                    run{
                        if(festivalList.culturalEventInfo.row.isNotEmpty()) {
                            val festivalAdapter = FestivalRecyclerAdapter(this@FestivalListFragment, festivalList, it)
                            adapter = festivalAdapter
                            festivalAdapter.refreshFestival()
                        }
                    }
                }
            }
            binding.progressBar.visibility = View.GONE // rest 완료시 progressbar 제거
        }
        viewModel.errorMessage.observe(viewLifecycleOwner){
            Log.e(TAG,it)
        }
        viewModel.isLoading.observe(viewLifecycleOwner){
            if(it){
                binding.progressBar.visibility = View.VISIBLE
            }else{
                binding.progressBar.visibility = View.GONE
            }
        }
    }
    // 리스너 셋팅 함수
    private fun listenerSetup(){
        // 날짜 선택
        binding.inputDate.setOnClickListener {
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                binding.inputDate.text = dateStringFormat(month,dayOfMonth) // 날짜 버튼 text 변경
                viewModel.getAllFestivalFromViewModel(category,year,month+1,dayOfMonth)
            }
            this.context?.let { it1 -> makeDatePickerDialog(it1,dateSetListener) }
        }
        // 카테고리 선택 스피너
        binding.categorySpinner.onItemSelectedListener = object: OnItemSelectedListener{
            val categoryList = resources.getStringArray(R.array.category)
            // spinner 에서 값 선택시
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long){
                category =categoryList[position]
                if(category == "전체") // pai 주소에서 전체는 category = null 이므로 "" 로변환
                    category = ""
                viewModel.getAllFestivalFromViewModel(category) // rest 재호출
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}