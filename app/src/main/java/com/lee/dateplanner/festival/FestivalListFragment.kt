package com.lee.dateplanner.festival

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lee.dateplanner.common.makeDatePickerDialog
import com.lee.dateplanner.databinding.FestivallistFragmentLayoutBinding
import com.lee.dateplanner.festival.adapter.FestivalRecyclerAdapter
import com.lee.dateplanner.festival.network.FestivalRetrofitService
import java.util.*

class FestivalListFragment:Fragment() {
    companion object{
        fun newInstance() = FestivalListFragment()
    }
    private lateinit var binding: FestivallistFragmentLayoutBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FestivallistFragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: FestivalViewModel = ViewModelProvider(this, FestivalViewModelFactory(
            FestivalRepository(FestivalRetrofitService.getInstance())
        )
        ).get(FestivalViewModel::class.java)

        listenerSetup()
        viewModel.festivalList.observe(viewLifecycleOwner){
            with(binding.festivalList){
                run{
                    val festivalAdapter = FestivalRecyclerAdapter(it)
                    adapter = festivalAdapter
                    festivalAdapter
                }
            }
            binding.progressBar.visibility = View.GONE // rest 완료시 progressbar 제거
        }

        viewModel.errorMessage.observe(viewLifecycleOwner){
            Log.e(ContentValues.TAG,it)
        }
        viewModel.getAllFestivalFromViewModel()
    }
    // 리스너 셋팅 함수
    private fun listenerSetup(){
        // 날짜 선택
        binding.inputDate.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val date = "${month+1} 월 $dayOfMonth 일"
                binding.inputDate.text = date
            }
            this.context?.let { it1 -> DatePickerDialog(it1,dateSetListener,cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show() }
        }
        // 카테고리 선택 스피너
    }
}