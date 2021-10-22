package com.example.tfs.ui.topic

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.customviews.PostLayout

class PostVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val postView: PostLayout = itemView.findViewById<PostLayout>(R.id.cvPost)
}