package com.example.tfs.ui.topic


import android.util.Log
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
    private var upAnchorId = -1
    private var downAnchorId = -1
    private var currentDataSize = 0
    private lateinit var streamName: String
    private lateinit var topicName: String

    private val repository = RepositoryImpl()
    private val topicToItemMapper: TopicToItemMapper = TopicToItemMapper()

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val initialFetchTopic: PublishSubject<Pair<String, String>> = PublishSubject.create()
    private val pagingFetch: PublishSubject<PagingQuery> = PublishSubject.create()

    val topicScreenState: LiveData<TopicScreenState> get() = _topicScreenState
    private var _topicScreenState: MutableLiveData<TopicScreenState> = MutableLiveData()

    init {
        subscribeToFetchTopic()
        subscribeToUploadTopic()
    }

    fun fetchTopic(fetchQuery: Pair<String, String>) {
        streamName = fetchQuery.first
        topicName = fetchQuery.second
        initialFetchTopic.onNext(fetchQuery) //TODO - get 50 posts from newest
    }

    fun uploadTopic(isDownScroll: Boolean = true) {
        Log.i("UploadScroll", "Function called: uploadTopic() $isDownScroll $currentDataSize $downAnchorId $upAnchorId")
        if (currentDataSize < MAX_LIST_SIZE) {
            if (isDownScroll) initialFetchTopic.onNext(streamName to topicName)
        } else {
            pagingFetch.onNext(PagingQuery(streamName,
                topicName,
                if (isDownScroll) downAnchorId else upAnchorId,
                isDownScroll))
        }
    }

    private fun subscribeToUploadTopic() {
        pagingFetch

            .subscribeOn(Schedulers.io())
            .doOnNext { _topicScreenState.postValue(TopicScreenState.Loading) }
            .switchMap { repository.uploadTopic(it) }
            .map(topicToItemMapper)
            .observeOn(AndroidSchedulers.mainThread(), true)
            .subscribeBy(
                onNext = { uiTopicObject ->
                    upAnchorId = uiTopicObject.upAnchorId
                    downAnchorId = uiTopicObject.downAnchorId
                    currentDataSize = uiTopicObject.localDataLength
                    _topicScreenState.value = TopicScreenState.Result(uiTopicObject.itemList)
                },
                onError = { _topicScreenState.value = TopicScreenState.Error(it) }
            )
            .addTo(compositeDisposable)
    }

    private fun subscribeToFetchTopic() {
        initialFetchTopic
            .subscribeOn(Schedulers.io())
            .doOnNext { _topicScreenState.postValue(TopicScreenState.Loading) }
            .switchMap { (streamName, topicName) -> repository.fetchTopic(streamName, topicName) }
            .map(topicToItemMapper)
            .observeOn(AndroidSchedulers.mainThread(), true)
            .subscribeBy(
                onNext = { uiTopicObject ->
                    upAnchorId = uiTopicObject.upAnchorId
                    downAnchorId = uiTopicObject.downAnchorId
                    currentDataSize = uiTopicObject.localDataLength
                    _topicScreenState.value = TopicScreenState.Result(uiTopicObject.itemList)
                },
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

data class PagingQuery(
    val streamName: String,
    val topicName: String,
    val anchorId: Int = 0,
    val isDownScroll: Boolean = true,
    val isInitial: Boolean = false
)

private const val MAX_LIST_SIZE = 50
