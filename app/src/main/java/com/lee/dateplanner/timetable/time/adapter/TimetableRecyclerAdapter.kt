package com.lee.dateplanner.timetable.time.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lee.dateplanner.databinding.TimetableRecyclerLayoutBinding
import com.lee.dateplanner.timetable.time.room.Timetable

class TimetableRecyclerAdapter(timetable: Timetable): RecyclerView.Adapter<TimetableViewHolder>() {
    private lateinit var binding: TimetableRecyclerLayoutBinding
    private var timetableList: List<Timetable>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimetableViewHolder {
        binding = TimetableRecyclerLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TimetableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimetableViewHolder, position: Int) {

        with(holder){

        }
    }

    override fun getItemCount() = if(timetableList == null) 0 else timetableList!!.size
}