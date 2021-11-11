package com.example.tfs.ui.streams.adapter

import com.example.tfs.domain.streams.StreamItemList

internal class StreamToItemMapper : (List<StreamItemList.StreamItem>) -> (List<StreamItemList>) {

    override fun invoke(streams: List<StreamItemList.StreamItem>): List<StreamItemList> {
        val domainStreamList = mutableListOf<StreamItemList>()

        streams.forEach { stream ->
            domainStreamList.add(stream)
            stream.topics.forEach {
                domainStreamList.add(it)
            }
        }
        return domainStreamList.toList()
    }
}

