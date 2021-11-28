package com.example.tfs.ui.stream.adapter

import com.example.tfs.domain.streams.StreamListItem

class TopicItemBinder(private val onClickItem: (StreamListItem) -> Unit) {

    fun bind(topicItemViewHolder: TopicItemViewHolder, item: StreamListItem.TopicItem) {

        topicItemViewHolder.setTopicName(item.name)

        /*topicItemViewHolder.setTopicStat(item.max_id)

        topicItemViewHolder.setTopicActivityColor(ContextCompat.getColor(
            topicItemViewHolder.itemView.context,
            if(item.max_id in 0..100) R.color.topic_bg_color else R.color.hot_topic_bg_color))*/

        topicItemViewHolder.setTopicClickListener(onClickItem, item)
    }
}