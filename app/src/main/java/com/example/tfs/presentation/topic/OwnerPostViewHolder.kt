package com.example.tfs.presentation.topic

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.presentation.topic.customviews.OwnerPostLayout

class OwnerPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val ownerPostView: OwnerPostLayout = itemView.findViewById(R.id.cvOwnerPost)
}