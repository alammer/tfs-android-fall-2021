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

    init {
        _streamList.postValue(repository.getMockDomainStreamList(isSubscribed, expandedStreams))
    }

    fun showSubscribed(showSubscribed: Boolean) {
        if (showSubscribed != isSubscribed) {
            isSubscribed = showSubscribed
            _streamList.postValue(repository.getMockDomainStreamList(isSubscribed, expandedStreams))
        }
    }

    fun changeStreamMode(streamName: String) {
        if (!expandedStreams.remove(streamName)) expandedStreams.add(streamName)
        _streamList.postValue(repository.getMockDomainStreamList(isSubscribed, expandedStreams))
    }

}
