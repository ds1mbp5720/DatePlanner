package com.lee.dateplanner.festival

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lee.dateplanner.databinding.FestivallistFragmentLayoutBinding
import com.lee.dateplanner.festival.adapter.FestivalRecyclerAdapter
import com.lee.dateplanner.festival.network.FestivalRetrofitService
import com.lee.dateplanner.map.POIRepository
import com.lee.dateplanner.map.POIViewModel
import com.lee.dateplanner.map.POIViewModelFactory
import com.lee.dateplanner.map.adpter.POIRecyclerAdapter
import com.lee.dateplanner.map.network.POIRetrofitService

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

        viewModel.festivalList.observe(viewLifecycleOwner){
            with(binding.festivalList){
                run{
                    val festivalAdapter = FestivalRecyclerAdapter(it)
                    adapter = festivalAdapter
                    festivalAdapter
                }
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner){
            Log.e(ContentValues.TAG,it)
        }
        viewModel.getAllFestivalFromViewModel()
    }

}