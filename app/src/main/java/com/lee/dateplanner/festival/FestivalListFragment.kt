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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lee.dateplanner.R
import com.lee.dateplanner.common.dateStringFormat
import com.lee.dateplanner.common.makeDatePickerDialog
import com.lee.dateplanner.databinding.FestivallistFragmentLayoutBinding
import com.lee.dateplanner.festival.adapter.FestivalRecyclerAdapter
import com.lee.dateplanner.festival.data.FestivalListData
import com.lee.dateplanner.festival.network.FestivalRetrofitService

class FestivalListFragment:Fragment() {
    companion object{
        fun newInstance() = FestivalListFragment()
    }
    private lateinit var binding: FestivallistFragmentLayoutBinding
    lateinit var viewModel : FestivalViewModel
    private var year = 0
    private var month = 0
    private var day = 0
    private var category = ""
    private var recyclerCount = 0
    private var allFestivalList = mutableListOf<FestivalListData>()

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

        viewModel.getFestivalLocationFromViewModel()
        listenerSetup()
        // 행사 정보 처리
        viewModel.festivalList.observe(viewLifecycleOwner){
            val festivalList = it.culturalEventInfo
            for(i in 0 until festivalList.row.size){
                var festival = FestivalListData(festivalList.row[i].cODENAME,festivalList.row[i].dATE,festivalList.row[i].eNDDATE,festivalList.row[i].mAINIMG,
                    festivalList.row[i].oRGLINK,festivalList.row[i].pLACE,festivalList.row[i].rGSTDATE,festivalList.row[i].sTRTDATE,festivalList.row[i].tICKET,
                    festivalList.row[i].tITLE,festivalList.row[i].uSEFEE)
                allFestivalList.add(festival)
            }
            // 행사 장소 정보 처리
            viewModel.festivalPlaceList.observe(viewLifecycleOwner){
                with(binding.festivalList){
                    run{
                        if(festivalList.row.isNotEmpty()) {
                            val festivalAdapter = FestivalRecyclerAdapter(this@FestivalListFragment, allFestivalList,it)
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
                this.year = year
                this.month = month + 1
                this.day = dayOfMonth
                viewModel.getAllFestivalFromViewModel(category,this.year, this.month, this.day)
            }
            this.context?.let { it1 -> makeDatePickerDialog(it1,dateSetListener) }
        }
        // 카테고리 선택 스피너
        binding.categorySpinner.onItemSelectedListener = object: OnItemSelectedListener{
            val categoryList = resources.getStringArray(R.array.category)
            // spinner 값 선택시
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long){
                category =categoryList[position]
                if(category == "전체") // pai 주소에서 전체는 category = null 이므로 "" 로변환
                    category = ""
                viewModel.getAllFestivalFromViewModel(category,year, month, day) // rest 재호출
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    private fun recyclerPaging(){
        viewModel.getFestivalFromViewModelPaging(category,year,month,day, 1 + (20 * recyclerCount),20 * (recyclerCount + 1))
        binding.festivalList.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter?.itemCount
                if(lastVisibleItemPosition +1  == itemTotalCount){
                    recyclerCount ++
                    viewModel.getFestivalFromViewModelPaging(category,year,month,day, 1 + (20 * recyclerCount),20 * (recyclerCount + 1))
                }
            }
        })
    }
}