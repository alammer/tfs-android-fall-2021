package com.example.tfs.domain.topic

import android.util.Log
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject


class TopicInteractor @Inject constructor(private val topicRepository: TopicRepository) {

    private val topicToItemMapper: TopicToItemMapper = TopicToItemMapper()

    fun topic(stream: String, topic: String): Observable<UiTopicListObject> {
        return topicRepository.fetchTopic(stream, topic)
            .map(topicToItemMapper)
        //TODO("return specific value from DB for empty local cache")
    }

    fun nextPage(stream: String, topic: String, anchorId: Int): Single<UiTopicListObject> {
        Log.i("Topicsfetch", "Function called: nextPage()")
        return topicRepository.fetchNextPage(stream, topic, anchorId)
            .map(topicToItemMapper)
    }

    fun previousPage(stream: String, topic: String, anchorId: Int): Single<UiTopicListObject> {
        return topicRepository.fetchPrevPage(stream, topic, anchorId)
            .map(topicToItemMapper)
    }

    fun send(stream: String, topic: String, message: String): Single<UiTopicListObject> {
        return topicRepository.sendMessage(stream, topic, message)
            .map(topicToItemMapper)
    }

    fun update(postId: Int, emojiName: String, emojiCode: String): Single<UiTopicListObject> {
        return topicRepository.updateReaction(postId, emojiName, emojiCode)
            .map(topicToItemMapper)
    }
}