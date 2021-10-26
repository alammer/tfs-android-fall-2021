package com.example.tfs.presentation.topic

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.presentation.topic.customviews.PostLayout

class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val postView: PostLayout = itemView.findViewById<PostLayout>(R.id.cvPost)
}