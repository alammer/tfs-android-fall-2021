package com.example.tfs.data

import android.util.Log
import com.example.tfs.domain.streams.StreamItemList
import com.example.tfs.network.ApiService
import com.example.tfs.network.models.toDomainStream
import com.example.tfs.network.models.toDomainTopic
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import android.service.autofill.UserData

import io.reactivex.ObservableSource
import io.reactivex.functions.BiFunction


interface Repository {

//    fun cachedStreams()
//
//    fun searchStreams(
//        query: String,
//        isSubscribed: Boolean,
//        expanded: List<String>,
//    ): Observable<List<StreamItemList>>
}

class RepositoryImpl : Repository {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val rawStreams: MutableList<StreamItemList.StreamItem> = mutableListOf()
    private val subscribedStreams: MutableList<StreamItemList.StreamItem> = mutableListOf()

    private val newtworkService = ApiService.create()

    private fun fetchRawStreams(): List<StreamItemList.StreamItem> {
        newtworkService.getStreams()
            .subscribeOn(Schedulers.io())
            .map { response -> response.streams?.map { it.toDomainStream() } }
            .subscribe { streams -> streams?.apply { rawStreams.addAll(streams) } }
            .addTo(compositeDisposable)
        return rawStreams.toList()
    }

    private fun fetchSubscribedStreams(): List<StreamItemList.StreamItem> {
        newtworkService.getSubscribedStreams()
            .subscribeOn(Schedulers.io())
            .map { response -> response.streams?.map { it.toDomainStream() } }
            .subscribe { streams ->
                streams?.apply {
                    subscribedStreams.addAll(streams)
                }
            }
            .addTo(compositeDisposable)
        return subscribedStreams.toList()
    }

    private fun fetchTopicList(streamId: Int, streamName: String): List<StreamItemList.TopicItem> {
        val relatedTopicList = mutableListOf<StreamItemList.TopicItem>()
        newtworkService.getTopics(streamId)
            .subscribeOn(Schedulers.io())
            .map { response -> response.topicResponseList?.map { it.toDomainTopic(streamName) } }
            .subscribe { remoteStreams ->
                remoteStreams?.apply {
                    relatedTopicList.addAll(remoteStreams)
                }
            }
            .addTo(compositeDisposable)
        return relatedTopicList.toList()
    }

//    override fun cachedStreams() {
//        rawStreams.addAll(fetchRawStreams())
//        subscribedStreams.addAll(fetchSubscribedStreams())
//    }

//    override fun searchStreams(
//        query: String,
//        isSubscribed: Boolean,
//        expanded: List<String>,
//    ): Observable<List<StreamItemList>> {
//        newtworkService.getSubscribedStreams()
//            .subscribeOn(Schedulers.io())
//            .map { response -> response.streams?.map { it.toDomainStream() } }
//            .subscribe()
//            .addTo(compositeDisposable)
//    }

    private fun fetchTopics(streamId: Int, streamName: String) =
        newtworkService.getTopics(streamId)
            .subscribeOn(Schedulers.io())
            .map { response -> response.topicResponseList?.map { it.toDomainTopic(streamName) } }
            .toObservable()




    fun fetchSubscribed(query: String,
                                isSubscribed: Boolean,
                                expanded: List<String>): Observable<List<StreamItemList>> =
        newtworkService.getSubscribedStreams()
            .subscribeOn(Schedulers.io())
            .toObservable()
            .flatMap { streams -> Observable.fromIterable(streams.streams) }
            .filter { stream -> stream.name.contains(query)}
            .map { stream -> stream.toDomainStream() }



    private fun fetchDomainStreamList(
        streams: List<StreamItemList.StreamItem>,
        query: String,
        expandedStreams: List<String>,
    ): List<StreamItemList> {
        val domainStreamList = mutableListOf<StreamItemList>()

        streams.filter { it.name.contains(query) }.forEach { stream ->
            if (stream.name in expandedStreams) {
                val relatedTopics =
                    fetchTopicList(stream.id, stream.name)
                if (relatedTopics.isNotEmpty()) {
                    domainStreamList.add(stream.copy(expanded = true))
                    domainStreamList.addAll(relatedTopics)
                } else {
                    domainStreamList.add(stream)
                }
            } else {
                domainStreamList.add(stream)
            }
        }
        return domainStreamList.toList()
    }
}

//    private fun getMockDomainTopic(streamName: String, topicName: String): List<TopicItem> {
//        val topic =
//            remoteTopicList.firstOrNull { it.parentStreamName == streamName && it.name == topicName }
//                ?: return emptyList()
//        val datedPostList = mutableListOf<TopicItem>()
//        var startTopicDate = 0L
//        val currentDate = System.currentTimeMillis()
//
//        topic.postList.forEach {
//            if (it.timeStamp.startOfDay() > startTopicDate) {
//                startTopicDate = it.timeStamp.startOfDay()
//                if (startTopicDate.year < currentDate.year) {
//                    datedPostList.add(TopicItem.LocalDateItem(startTopicDate.fullDate))
//                } else {
//                    datedPostList.add(TopicItem.LocalDateItem(startTopicDate.shortDate))
//                }
//            }
//
//            if (it.isOwner) {
//                datedPostList.add(it.toDomainOwnerPost())
//            } else {
//                datedPostList.add(it.toDomainUserPost())
//            }
//        }
//        return datedPostList
//    }
