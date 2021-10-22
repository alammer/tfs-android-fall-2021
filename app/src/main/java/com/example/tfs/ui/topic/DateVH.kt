package com.example.tfs.ui.topic

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R

class DateVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val dateView: TextView = itemView.findViewById<TextView>(R.id.tvDateItem)
}