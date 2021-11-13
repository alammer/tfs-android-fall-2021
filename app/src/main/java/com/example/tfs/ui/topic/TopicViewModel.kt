package com.example.tfs.ui.topic

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tfs.data.RepositoryImpl
import com.example.tfs.ui.streams.adapter.StreamToItemMapper
import com.example.tfs.ui.topic.adapter.TopicToItemMapper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

internal class TopicViewModel : ViewModel() {

    private val repository = RepositoryImpl()
    private val topicToItemMapper: TopicToItemMapper = TopicToItemMapper()

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val searchTopic: PublishSubject<Pair<String, String>> = PublishSubject.create()

    val topicScreenState: LiveData<TopicScreenState> get() = _topicScreenState
    private var _topicScreenState: MutableLiveData<TopicScreenState> = MutableLiveData()

    init {
        subscribeToFetchTopic()
    }

    fun fetchTopic(fetchQuery: Pair<String, String>) {
        searchTopic.onNext(fetchQuery)
    }

    private fun subscribeToFetchTopic() {
        searchTopic
            .subscribeOn(Schedulers.io())
            .doOnNext { _topicScreenState.postValue(TopicScreenState.Loading) }
            .switchMap { (streamName, topicName) -> repository.fetchTopic(streamName, topicName) }
            .observeOn(AndroidSchedulers.mainThread())
            .map(topicToItemMapper)
            .subscribeBy(
                onNext = { _topicScreenState.value = TopicScreenState.Result(it) },
                onError = { _topicScreenState.value = TopicScreenState.Error(it) }
            )
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
