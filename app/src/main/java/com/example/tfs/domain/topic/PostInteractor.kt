package com.example.tfs.domain.topic

import com.example.tfs.database.entity.LocalPost
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject


class PostInteractor @Inject constructor(private val postRepository: PostRepository) {

    private val topicToUiItemMapper: TopicToUiItemMapper = TopicToUiItemMapper()

    fun fetchLocalTopic(stream: String, topic: String): Single<UiTopicListObject> {
        return postRepository.fetchLocalTopic(stream, topic)
            .map(topicToUiItemMapper)
    }

    fun fetchRemoteTopic(stream: String, topic: String): Single<UiTopicListObject> {
        return postRepository.fetchRemoteTopic(stream, topic)
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

    fun sendNewPost(
        stream: String,
        topic: String,
        message: String,
    ): Single<UiTopicListObject> {
        return postRepository.sendNewPost(stream, topic, message)
            .map(topicToUiItemMapper)
    }

    fun sendEditPost(
        message: String,
        postId: Int,
        upAnchor: Int,
        stream: String,
        topic: String
    ): Single<UiTopicListObject> {
        return postRepository.sendEditPost(message, postId, upAnchor, stream, topic)
            .map(topicToUiItemMapper)
    }

    fun deletePost(postId: Int): Single<UiTopicListObject> {
        return postRepository.deletePost(postId)
            .map(topicToUiItemMapper)
    }

    fun updatePost(postId: Int, emojiName: String, emojiCode: String): Single<UiTopicListObject> {
        return postRepository.updateReaction(postId, emojiName, emojiCode)
            .map(topicToUiItemMapper)
    }

    fun getPost(postId: Int): Maybe<LocalPost> {
        return postRepository.getPost(postId)
    }

    fun getTopicList(streamId: Int): Single<List<String>> {
        return postRepository.getTopicList(streamId)
    }

    fun movePost(stream: String, topic: String, postId: Int): Single<UiTopicListObject> {
        return postRepository.movePostToTopic(stream, topic, postId)
            .map(topicToUiItemMapper)
    }
}