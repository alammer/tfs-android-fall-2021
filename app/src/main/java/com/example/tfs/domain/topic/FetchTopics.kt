package com.example.tfs.domain.topic

import io.reactivex.Observable


class FetchTopics(private val topicRepository: TopicRepository) {

    private val topicToItemMapper: TopicToItemMapper = TopicToItemMapper()

    fun topic(stream: String, topic: String): Observable<TopicListObject> {
        return topicRepository.fetchTopic(stream, topic)
            .map{ topicToItemMapper(it, 1) }
    }

    fun nextPage(stream: String, topic: String, anchorId: Int): Observable<TopicListObject> {
        return topicRepository.fetchNextPage(stream, topic, anchorId)
            .map{ topicToItemMapper(it, 1) }
    }

    fun previousPage(stream: String, topic: String, anchorId: Int): Observable<TopicListObject> {
        return topicRepository.fetchPrevPage(stream, topic, anchorId)
            .map{ topicToItemMapper(it, 1) }
    }

    fun send(stream: String, topic: String, message: String): Observable<TopicListObject> {
        return topicRepository.sendMessage(stream, topic, message)
            .map{ topicToItemMapper(it, 1) }
    }

    fun update(stream: String, topic: String, postId: Int, emojiCode: String): Observable<TopicListObject> {
        return topicRepository.changeReaction(stream, topic, postId, emojiCode)
            .map{ topicToItemMapper(it, 1) }
    }
}