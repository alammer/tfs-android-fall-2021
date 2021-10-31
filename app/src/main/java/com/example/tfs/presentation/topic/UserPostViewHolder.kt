package com.example.tfs.presentation.topic

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.presentation.topic.customviews.UserPostLayout

class UserPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val userPostView: UserPostLayout = itemView.findViewById<UserPostLayout>(R.id.cvUserPost)

    fun getViewHolderRootLayout() = userPostView
}