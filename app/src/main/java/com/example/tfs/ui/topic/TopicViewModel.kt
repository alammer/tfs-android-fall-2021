package com.example.tfs.ui.topic


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tfs.domain.RepositoryImpl
import com.example.tfs.ui.topic.adapter.TopicToItemMapper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject


internal class TopicViewModel : ViewModel() {
    //remove field after db implement
    private var lastEmoji: String = ""
    private var currentAnchorPost = 0
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

    fun updateTopic(isDownScroll: Boolean = true) {

    }


    private fun subscribeToFetchTopic() {
        searchTopic
            .subscribeOn(Schedulers.io())
            .doOnNext { _topicScreenState.postValue(TopicScreenState.Loading) }
            .switchMap { (streamName, topicName) -> repository.fetchTopic(streamName, topicName) }
            .map(topicToItemMapper)
            .observeOn(AndroidSchedulers.mainThread(), true)
            .subscribeBy(
                onNext = { _topicScreenState.value = TopicScreenState.Result(it) },
                onError = { _topicScreenState.value = TopicScreenState.Error(it) }
            )
            .addTo(compositeDisposable)
    }

    fun sendMessage(streamName: String, topicName: String, content: String) {
        repository.sendMessage(streamName, topicName, content)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { _topicScreenState.value = TopicScreenState.Error(it) }
            )
            .addTo(compositeDisposable)
    }

    fun addReaction(messageId: Int, emojiName: String, emojiCode: String) {
        lastEmoji = emojiName
        repository.addReaction(messageId, emojiName, emojiCode)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { _topicScreenState.value = TopicScreenState.Error(it) }
            )
            .addTo(compositeDisposable)
    }

    fun updateReaction(messageId: Int, emojiCode: String) {
        repository.removeReaction(messageId, lastEmoji, emojiCode)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { _topicScreenState.value = TopicScreenState.Error(it) }
            )
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
