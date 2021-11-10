package com.example.tfs.ui.topic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tfs.data.RepositoryImpl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

internal class TopicViewModel : ViewModel() {

    private val repository = RepositoryImpl()

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val searchTopic: PublishSubject<Pair<String, String>> = PublishSubject.create()

    val topicScreenState: LiveData<TopicScreenState> get() = _topicScreenState
    private var _topicScreenState: MutableLiveData<TopicScreenState> = MutableLiveData()

    init {
        //subscribeToSearchTopic()
    }

    fun fetchTopic(searchQuery: Pair<String, String>) {
        searchTopic.onNext(searchQuery)
    }

//    private fun subscribeToSearchTopic() {
//        searchTopic
//            .subscribeOn(Schedulers.io())
//            .doOnNext { _topicScreenState.postValue(TopicScreenState.Loading) }
//            .debounce(1500, TimeUnit.MILLISECONDS, Schedulers.io())
//            .switchMap { (streamName, topicName) -> repository.fetchTopic(streamName, topicName) }
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeBy(
//                onNext = { _topicScreenState.value = TopicScreenState.Result(it) },
//                onError = { _topicScreenState.value = TopicScreenState.Error(it) }
//            )
//            .addTo(compositeDisposable)
//    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
