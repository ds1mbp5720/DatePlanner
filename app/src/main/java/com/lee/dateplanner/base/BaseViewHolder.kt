package com.lee.dateplanner.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun onBindViewHolder(data: Any?) {}
}