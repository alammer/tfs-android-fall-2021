package com.example.tfs.domain.streams

import com.example.tfs.ui.stream.adapter.base.StreamListItem


data class DomainStream(
    val id: Int,
    val name: String,
    val topics: List<String> = emptyList(),
    val expanded: Boolean = false,
) : StreamListItem

data class DomainTopic(
    val name: String,
    val parentStreamName: String,
) : StreamListItem

