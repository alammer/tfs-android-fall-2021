package com.example.tfs.presentation.streams

import androidx.core.content.ContextCompat
import com.example.tfs.R
import com.example.tfs.data.StreamItemList

class TopicItemViewHolderBinder (private val onClickItem: (StreamItemList) -> Unit) {

    fun bind(topicItemViewHolder: TopicItemViewHolder, item: StreamItemList.TopicItem) {

        topicItemViewHolder.setTopicName(item.topicName)

        topicItemViewHolder.setTopicStat(item.messageStat)

        topicItemViewHolder.setTopicActivityColor(ContextCompat.getColor(
            topicItemViewHolder.itemView.context,
            if(item.messageStat in 0..100) R.color.topic_bg_color else R.color.hot_topic_bg_color))

        topicItemViewHolder.setTopicClickListener(onClickItem, item)
    }
}