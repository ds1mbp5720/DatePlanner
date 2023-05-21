package com.lee.dateplanner.festival

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import com.lee.dateplanner.R
import com.lee.dateplanner.base.BaseFragment
import com.lee.dateplanner.common.*
import com.lee.dateplanner.databinding.FestivallistFragmentLayoutBinding
import com.lee.dateplanner.festival.adapter.FestivalRecyclerAdapter
import com.lee.dateplanner.festival.data.FestivalInfoData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FestivalListFragment : BaseFragment<FestivallistFragmentLayoutBinding, FestivalViewModel>() {
    companion object {
        fun newInstance() = FestivalListFragment()
    }

    override val layoutId: Int = R.layout.festivallist_fragment_layout
    override val viewModel: FestivalViewModel by viewModels()
    private val adapter = FestivalRecyclerAdapter(this)
    private var festivalList = mutableListOf<FestivalInfoData.CulturalEventInfo.Row>()
    private var year = 0
    private var month = 0
    private var day = 0
    private var category = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.setViewModel(viewModel)
        viewModel.getAllFestivalFromViewModel(category, this.year, this.month, this.day)
        viewModel.getFestivalLocationFromViewModel()
        listenerSetup()
        dataBinding.festivalList.adapter = adapter
    }

    override fun initObserve() {
        super.initObserve()

        viewModel.festivalList.observe(this) {
            festivalList.clear()
            festivalList.addAll(it.culturalEventInfo.row)
            if(year == 0 && month == 0 && day == 0)
                adapter.setFestivalData(festivalList.filterByTodayDate())
            else
                adapter.setFestivalData(festivalList.filterByDate(this.year,this.month,this.day))
        }
        viewModel.festivalPlaceList.observe(this) {
            adapter.setFestivalSpaceData(it.culturalSpaceInfo.row.toMutableList())
        }
        viewModel.errorMessage.observe(this) {
            Log.e(TAG, it)
        }
        viewModel.isLoading.observe(this) {
            if (it) {
                dataBinding.progressBar.visibility = View.VISIBLE
            } else {
                dataBinding.progressBar.visibility = View.GONE
            }
        }
        viewModel.getLocationCheck.observe(this){
            if(it==true) context?.toast(R.string.findFestivalLocation)
            else context?.toast(R.string.notFindFestivalLocation)
        }
        viewModel.eventClick.observe(this){
            when(it){
                FestivalViewModel.Event.Date -> {
                    val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        dataBinding.inputDate.text = dateStringFormat(month, dayOfMonth) // 날짜 버튼 text 변경
                        this.year = year
                        this.month = month + 1
                        this.day = dayOfMonth
                        adapter.setFestivalData(festivalList.filterByDate(this.year,this.month,this.day))
                    }
                    this.context?.let { it1 -> makeDatePickerDialog(it1, dateSetListener) }
                }
                FestivalViewModel.Event.Category -> {

                }
            }
        }
    }
    // 리스너 셋팅 함수
    private fun listenerSetup() {
        // 카테고리 선택 스피너
        dataBinding.categorySpinner.onItemSelectedListener = object : OnItemSelectedListener {
            val categoryList = resources.getStringArray(R.array.category)
            // spinner 값 선택시
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                category = categoryList[position]
                if (category == "전체") // pai 주소에서 전체는 category = null 이므로 "" 로변환
                    category = ""
                viewModel.getAllFestivalFromViewModel(category, year, month, day) // rest 재호출
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}