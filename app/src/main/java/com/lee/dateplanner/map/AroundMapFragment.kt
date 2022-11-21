package com.lee.dateplanner.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lee.dateplanner.databinding.AroundinfoMapFragmentLayoutBinding

class AroundMapFragment:Fragment() {
    companion object{
        fun newInstance() = AroundMapFragment()
    }
    private lateinit var binding: AroundinfoMapFragmentLayoutBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AroundinfoMapFragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }
}