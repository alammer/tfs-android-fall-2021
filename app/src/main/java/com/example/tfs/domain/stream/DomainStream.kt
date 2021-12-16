package com.example.tfs.domain.stream

import com.example.tfs.common.baseadapter.AdapterItem


data class DomainStream(
    val id: Int,
    val name: String,
    val topics: List<String> = emptyList(),
    val expanded: Boolean = false,
    val updated: Boolean = false,
) : AdapterItem

data class RelatedTopic(
    val name: String,
    val parentStreamName: String,
) : AdapterItem

