package com.example.tfs.domain.topic

import com.example.tfs.common.baseadapter.AdapterItem
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

    fun sendPost(stream: String, topic: String, message: String, downAnchor: Int): Single<UiTopicListObject> {
        return postRepository.sendMessage(stream, topic, message, downAnchor)
            .map(topicToUiItemMapper)
    }

    fun deletePost(postId: Int): Single<UiTopicListObject> {
        return postRepository.deleteMessage(postId)
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

    fun movePost(stream: String,topic: String, postId: Int): Single<UiTopicListObject> {
        return postRepository.movePostToTopic(stream, topic, postId)
            .map(topicToUiItemMapper)
    }
}