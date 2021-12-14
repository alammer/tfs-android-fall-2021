package com.example.tfs.domain.streams

import com.example.tfs.common.baseadapter.AdapterItem


data class DomainStream(
    val id: Int,
    val name: String,
    val topics: List<String> = emptyList(),
    val expanded: Boolean = false,
) : AdapterItem

data class DomainTopic(
    val name: String,
    val parentStreamName: String,
) : AdapterItem

