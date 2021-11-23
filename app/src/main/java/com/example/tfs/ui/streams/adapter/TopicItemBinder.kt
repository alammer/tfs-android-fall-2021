package com.example.tfs.ui.streams.adapter

import com.example.tfs.domain.streams.StreamItemList

class TopicItemBinder(private val onClickItem: (StreamItemList) -> Unit) {

    fun bind(topicItemViewHolder: TopicItemViewHolder, item: StreamItemList.TopicItem) {

        topicItemViewHolder.setTopicName(item.name)

        /*topicItemViewHolder.setTopicStat(item.max_id)

        topicItemViewHolder.setTopicActivityColor(ContextCompat.getColor(
            topicItemViewHolder.itemView.context,
            if(item.max_id in 0..100) R.color.topic_bg_color else R.color.hot_topic_bg_color))*/

        topicItemViewHolder.setTopicClickListener(onClickItem, item)
    }
}