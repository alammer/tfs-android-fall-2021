package com.example.tfs.domain.topic

import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject


class PostInteractor @Inject constructor(private val postRepository: PostRepository) {

    private val topicToUiItemMapper: TopicToUiItemMapper = TopicToUiItemMapper()

    fun fetchRecentPostList(stream: String, topic: String): Observable<UiTopicListObject> {
        return postRepository.fetchTopic(stream, topic)
            .map(topicToUiItemMapper)
    }

    fun fetchNextPage(stream: String, topic: String, anchorId: Int): Single<UiTopicListObject> {
        return postRepository.fetchNextPage(stream, topic, anchorId)
            .map(topicToUiItemMapper)
    }

    fun fetchPreviousPage(stream: String, topic: String, anchorId: Int): Single<UiTopicListObject> {
        return postRepository.fetchPrevPage(stream, topic, anchorId)
            .map(topicToUiItemMapper)
    }

    fun sendPost(stream: String, topic: String, message: String): Single<UiTopicListObject> {
        return postRepository.sendMessage(stream, topic, message)
            .map(topicToUiItemMapper)
    }

    fun updatePost(postId: Int, emojiName: String, emojiCode: String): Single<UiTopicListObject> {
        return postRepository.updateReaction(postId, emojiName, emojiCode)
            .map(topicToUiItemMapper)
    }
}