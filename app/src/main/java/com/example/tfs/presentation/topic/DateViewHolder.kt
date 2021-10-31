package com.example.tfs.presentation.topic

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R

class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val dateView: TextView = itemView.findViewById<TextView>(R.id.tvDateItem)

    fun setDate(date: String) {
        dateView.text = date
    }
}