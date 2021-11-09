package com.example.tfs.ui.topic

import com.example.tfs.domain.topic.TopicItem

internal sealed class TopicScreenState {

    class Result(val items: List<TopicItem>) : TopicScreenState()

    object Loading : TopicScreenState()

    class Error(val error: Throwable) : TopicScreenState()
}