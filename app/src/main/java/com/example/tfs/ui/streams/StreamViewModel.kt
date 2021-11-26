package com.example.tfs.ui.streams

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tfs.database.entity.LocalOwner
import com.example.tfs.domain.streams.StreamRepositoryImpl
import com.example.tfs.domain.streams.StreamToItemMapper
import com.example.tfs.ui.streams.viewpager.StreamScreenState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

internal class StreamViewModel : ViewModel() {

    private val repository = StreamRepositoryImpl()
    private val streamToItemMapper: StreamToItemMapper = StreamToItemMapper()
    private val expandedStreams: MutableList<Int> = mutableListOf()
    private var isSubscribed = true
    private var currentSearchQuery = ""

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val searchStream: PublishSubject<QueryKey> = PublishSubject.create()

    val streamScreenState: LiveData<StreamScreenState> get() = _streamScreenState
    private var _streamScreenState: MutableLiveData<StreamScreenState> = MutableLiveData()

    val owner: LiveData<LocalOwner?> get() = _owner
    private var _owner: MutableLiveData<LocalOwner?> = MutableLiveData()

    init {
        subscribeToSearchStreams()
    }

    fun showSubscribed(subscribed: Boolean) {
        isSubscribed = subscribed
        searchStream.onNext(QueryKey(currentSearchQuery,
            isSubscribed,
            expandedStreams.toList()))
    }

    fun changeStreamMode(streamId: Int) {
        if (!expandedStreams.remove(streamId)) expandedStreams.add(streamId)
        searchStream.onNext(QueryKey(currentSearchQuery,
            isSubscribed,
            expandedStreams.toList()))
    }

    fun fetchStreams(searchQuery: String) {
        currentSearchQuery = searchQuery
        searchStream.onNext(QueryKey(currentSearchQuery,
            isSubscribed,
            expandedStreams.toList()))
    }

    private fun subscribeToSearchStreams() {
        searchStream
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .doOnNext { _streamScreenState.postValue(StreamScreenState.Loading) }
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .switchMap { query ->
                repository.fetchStreams(query.queryString,
                    query.isSubscribed, )
            }
            .map(streamToItemMapper)
            .observeOn(AndroidSchedulers.mainThread(), true)
            .subscribeBy(
                onNext = { _streamScreenState.value = StreamScreenState.Result(it) },
                onError = { _streamScreenState.value = StreamScreenState.Error(it) }
            )
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    private data class QueryKey(
        val queryString: String = INITIAL_QUERY,
        val isSubscribed: Boolean = true,
        val expandedStream: List<Int>,
    )

    companion object {

        const val INITIAL_QUERY: String = ""
    }
}
