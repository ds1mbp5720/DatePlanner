package com.lee.dateplanner.timetable.insert.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lee.dateplanner.common.toastMessage
import com.lee.dateplanner.databinding.SelectDialogRecyclerBinding
import com.lee.dateplanner.timetable.insert.InsertTimeSheetActivity
import com.lee.dateplanner.timetable.time.room.Timetable
import java.util.*

class SelectTimeTableAdapter(private val owner: InsertTimeSheetActivity,private var dialog:Dialog): RecyclerView.Adapter<SelectTimeTableViewHolder>() {
    private lateinit var binding: SelectDialogRecyclerBinding
    private var timetableList: List<Timetable>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectTimeTableViewHolder {
        binding = SelectDialogRecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SelectTimeTableViewHolder(binding)
    }
    override fun onBindViewHolder(holder: SelectTimeTableViewHolder, position: Int) {
        with(holder.binding) {
            timetableName.text = timetableList!![position].date
            timetableSelectBtn.setOnClickListener {
                owner.setBtnTypeAPI(timetableList!![position], timetableList!![position].id)
                toastMessage("${timetableList!![position].date}에 일정이 추가되었습니다.")
                dialog.dismiss()
                owner.finish()
            }
        }
    }

    override fun getItemCount() = if(timetableList == null) 0 else timetableList!!.size

    @SuppressLint("NotifyDataSetChanged")
    fun setTimetableList(timetable: MutableList<Timetable>){
        timetableList = timetable
        notifyDataSetChanged()
    }
}