package com.example.tfs.ui.topic.elm

import com.example.tfs.domain.topic.FetchTopics
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat

class TopicActor(private val fetchTopics: FetchTopics) :
    ActorCompat<Command, TopicEvent.Internal>
{
    override fun execute(command: Command): Observable<TopicEvent.Internal> = when (command) {
        is Command.FetchTopic -> {
            fetchTopics.topic(command.streamName, command.topicName)
                .mapEvents(TopicEvent.Internal::TopicLoadingComplete, TopicEvent.Internal::TopicLoadingError)
        }
        is Command.FetchNextPage -> {
            fetchTopics.nextPage(command.streamName, command.topicName, command.downAnchor)
                .mapEvents(TopicEvent.Internal::TopicUpdatingComplete, TopicEvent.Internal::TopicUpdatingError)
        }
        is Command.FetchPrevPage -> {
            fetchTopics.previousPage(command.streamName, command.topicName, command.upAnchor)
                .mapEvents(TopicEvent.Internal::TopicUpdatingComplete, TopicEvent.Internal::TopicUpdatingError)
        }
        is Command.SendMessage -> {
            fetchTopics.send(command.streamName, command.topicName, command.message)
                .mapEvents(TopicEvent.Internal::TopicUpdatingComplete, TopicEvent.Internal::TopicUpdatingError)
        }
        is Command.UpdateReaction -> {
            fetchTopics.update(command.postId, command.emojiName, command.emojiCode)
                .mapEvents(TopicEvent.Internal::TopicUpdatingComplete, TopicEvent.Internal::TopicUpdatingError)
        }
    }
}