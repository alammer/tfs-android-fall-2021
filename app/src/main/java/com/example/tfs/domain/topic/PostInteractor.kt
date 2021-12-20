package com.example.tfs.domain.topic

import io.reactivex.Single
import javax.inject.Inject


class PostInteractor @Inject constructor(private val postRepository: PostRepository) {

    private val topicToUiItemMapper: TopicToUiItemMapper = TopicToUiItemMapper()

    fun fetchLocalTopic(stream: String, topic: String): Single<UiTopicListObject> {
        return postRepository.fetchLocalTopic(stream, topic)
            .map(topicToUiItemMapper)
    }

    fun getRemoteTopic(stream: String, topic: String): Single<UiTopicListObject> {
        return postRepository.getRemoteTopic(stream, topic)
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

    fun deletePost(stream: String, topic: String, postId: Int): Single<UiTopicListObject> {
        return postRepository.deleteMessage(stream, topic, postId)
            .map(topicToUiItemMapper)
    }

    fun updatePost(stream: String, topic: String, postId: Int, emojiName: String, emojiCode: String): Single<UiTopicListObject> {
        return postRepository.updateReaction(stream, topic, postId, emojiName, emojiCode)
            .map(topicToUiItemMapper)
    }
}