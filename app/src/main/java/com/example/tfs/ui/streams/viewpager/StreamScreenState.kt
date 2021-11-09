package com.example.tfs.ui.streams.viewpager

import com.example.tfs.domain.streams.StreamItemList

internal sealed class StreamScreenState {

    class Result(val items: List<StreamItemList>) : StreamScreenState()

    object Loading : StreamScreenState()

    class Error(val error: Throwable) : StreamScreenState()
}