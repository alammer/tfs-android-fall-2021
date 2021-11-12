package com.example.tfs.ui.topic

import com.example.tfs.domain.topic.PostItem

internal sealed class TopicScreenState {

    class Result(val items: List<PostItem>) : TopicScreenState()

    object Loading : TopicScreenState()

    class Error(val error: Throwable) : TopicScreenState()
}