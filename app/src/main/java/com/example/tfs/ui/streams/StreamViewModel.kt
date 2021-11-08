package com.example.tfs.ui.streams

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tfs.data.StreamRepositoryImpl
import com.example.tfs.domain.streams.StreamItemList

class StreamViewModel : ViewModel() {

    private val repository = StreamRepositoryImpl
    private val expandedStreams: MutableList<String> = mutableListOf()
    private var isSubscribed = true

    val streamList: LiveData<List<StreamItemList>?> get() = _streamList
    private val _streamList = MutableLiveData<List<StreamItemList>?>()

    fun showSubscribed(showSubscribed: Boolean) {
        isSubscribed = showSubscribed
        _streamList.value = repository.getMockDomainStreamList(showSubscribed, expandedStreams)
    }

    fun changeStreamMode(streamName: String) {
        if (!expandedStreams.remove(streamName)) expandedStreams.add(streamName)
        _streamList.value = repository.getMockDomainStreamList(isSubscribed, expandedStreams)
    }

}
