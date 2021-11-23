package com.example.tfs.ui.topic


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tfs.database.entity.LocalReaction
import com.example.tfs.domain.topic.TopicRepositoryImpl
import com.example.tfs.ui.topic.adapter.TopicToItemMapper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit


internal class TopicViewModel : ViewModel() {

    private var upAnchorId = -1
    private var downAnchorId = -1
    private var currentDataSize = 0
    private lateinit var streamName: String
    private lateinit var topicName: String
    var ownerId: Int = -1

    private val repository = TopicRepositoryImpl()
    private val topicToItemMapper: TopicToItemMapper = TopicToItemMapper()

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val initialFetchTopic: PublishSubject<Pair<String, String>> = PublishSubject.create()
    private val pagingFetch: PublishSubject<PagingQuery> = PublishSubject.create()

    val topicScreenState: LiveData<TopicScreenState> get() = _topicScreenState
    private var _topicScreenState: MutableLiveData<TopicScreenState> = MutableLiveData()

    private val reactionCache: MutableList<LocalReaction> = mutableListOf()

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
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .switchMap { repository.uploadTopic(it) }
            .doAfterNext { postList -> reactionCache(postList.flatMap { it.reaction }) }
            .map { topicToItemMapper(it, ownerId) }
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
            .doAfterNext { postList -> reactionCache(postList.flatMap { it.reaction }) }
            .map { topicToItemMapper(it, ownerId) }
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

    private fun reactionCache(reactionList: List<LocalReaction>) {
        reactionCache.clear()
        reactionCache.addAll(reactionList)
    }

    fun sendMessage(streamName: String, topicName: String, content: String) {
        repository.sendMessage(streamName,
            topicName,
            content,
            ownerId,
            System.currentTimeMillis() * 1000L)
            .doOnSubscribe { _topicScreenState.postValue(TopicScreenState.Loading) }
            .map { topicToItemMapper(it, ownerId) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { _topicScreenState.value = TopicScreenState.Error(it) },
                onSuccess = { uiTopicObject ->
                    upAnchorId = uiTopicObject.upAnchorId
                    downAnchorId = uiTopicObject.downAnchorId
                    currentDataSize = uiTopicObject.localDataLength
                    _topicScreenState.value = TopicScreenState.Result(uiTopicObject.itemList)
                }
            )
            .addTo(compositeDisposable)
    }

    fun updateReaction(messageId: Int, emojiName: String, emojiCode: String) {
        getAlreadyClickedReaction(messageId, emojiCode)?.run {
            removeReaction(messageId, name, code)
        } ?: addReaction(messageId, emojiName, emojiCode)
    }

    private fun addReaction(messageId: Int, emojiName: String, emojiCode: String) {
        val reactionName =
            if (emojiName.isNotBlank()) emojiName else reactionCache.first { it.code == emojiCode }.name
        repository.addReaction(messageId, reactionName, emojiCode, ownerId)
            .doOnSuccess { postList -> reactionCache(postList.flatMap { it.reaction }) }
            .map { topicToItemMapper(it, ownerId) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { _topicScreenState.value = TopicScreenState.Error(it) },
                onSuccess = { uiTopicObject ->
                    _topicScreenState.value = TopicScreenState.Result(uiTopicObject.itemList)
                }
            )
            .addTo(compositeDisposable)
    }

    private fun removeReaction(messageId: Int, emojiName: String, emojiCode: String) {
        repository.removeReaction(messageId, emojiName, emojiCode, ownerId)
            .doOnSuccess { postList -> reactionCache(postList.flatMap { it.reaction }) }
            .map { topicToItemMapper(it, ownerId) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { _topicScreenState.value = TopicScreenState.Error(it) },
                onSuccess = { uiTopicObject ->
                    _topicScreenState.value = TopicScreenState.Result(uiTopicObject.itemList)
                }
            )
            .addTo(compositeDisposable)
    }

    private fun getAlreadyClickedReaction(messageId: Int, emojiCode: String) =
        reactionCache
            .firstOrNull { reaction -> reaction.code == emojiCode && reaction.postId == messageId && reaction.userId == ownerId }

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
    val isInitial: Boolean = false,
)

private const val MAX_LIST_SIZE = 50
