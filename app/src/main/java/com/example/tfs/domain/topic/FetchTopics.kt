package com.example.tfs.domain.topic

import io.reactivex.Observable


class FetchTopics(private val topicRepository: TopicRepository) {

    private val topicToItemMapper: TopicToItemMapper = TopicToItemMapper()

    fun topic(stream: String, topic: String): Observable<UiTopicListObject> {
        return topicRepository.fetchTopic(stream, topic)
            .map(topicToItemMapper)
    }

    fun nextPage(stream: String, topic: String, anchorId: Int): Observable<UiTopicListObject> {
        return topicRepository.fetchNextPage(stream, topic, anchorId)
            .map(topicToItemMapper)
    }

    fun previousPage(stream: String, topic: String, anchorId: Int): Observable<UiTopicListObject> {
        return topicRepository.fetchPrevPage(stream, topic, anchorId)
            .map(topicToItemMapper)
    }

    fun send(stream: String, topic: String, message: String): Observable<UiTopicListObject> {
        return topicRepository.sendMessage(stream, topic, message)
            .map(topicToItemMapper)
    }

    fun update(postId: Int, emojiName: String, emojiCode: String): Observable<UiTopicListObject> {
        return topicRepository.updateReaction(postId, emojiName, emojiCode)
            .map(topicToItemMapper)
    }
}