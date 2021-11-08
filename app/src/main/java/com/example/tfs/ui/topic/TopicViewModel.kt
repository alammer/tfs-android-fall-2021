package com.example.tfs.ui.topic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tfs.data.StreamRepositoryImpl
import com.example.tfs.domain.topic.TopicItem

class TopicViewModel : ViewModel() {

    private val repository = StreamRepositoryImpl

    val topicList: LiveData<List<TopicItem>?> get() = _topicList
    private val _topicList = MutableLiveData<List<TopicItem>?>()

    fun fetchTopic(streamName: String, topicName: String) {
        _topicList.postValue(repository.getMockDomainTopic(streamName, topicName))
    }
}
