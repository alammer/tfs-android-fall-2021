package com.example.tfs.ui.topic.elm

import com.example.tfs.domain.topic.TopicInteractor
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat


class TopicActor /*@Inject constructor*/(
    private val topicInteractor: TopicInteractor,
) :
    ActorCompat<Command, TopicEvent.Internal> {
    override fun execute(command: Command): Observable<TopicEvent.Internal> = when (command) {
        is Command.FetchTopic -> {
            topicInteractor.topic(command.streamName, command.topicName)
                .mapEvents(TopicEvent.Internal::TopicLoadingComplete,
                    TopicEvent.Internal::TopicLoadingError)
        }
        is Command.FetchNextPage -> {
            topicInteractor.nextPage(command.streamName, command.topicName, command.downAnchor)
                .mapEvents(TopicEvent.Internal::TopicUpdatingComplete,
                    TopicEvent.Internal::TopicUpdatingError)
        }
        is Command.FetchPrevPage -> {
            topicInteractor.previousPage(command.streamName, command.topicName, command.upAnchor)
                .mapEvents(TopicEvent.Internal::TopicUpdatingComplete,
                    TopicEvent.Internal::TopicUpdatingError)
        }
        is Command.SendMessage -> {
            topicInteractor.send(command.streamName, command.topicName, command.message)
                .mapEvents(TopicEvent.Internal::TopicUpdatingComplete,
                    TopicEvent.Internal::TopicUpdatingError)
        }
        is Command.UpdateReaction -> {
            topicInteractor.update(command.postId, command.emojiName, command.emojiCode)
                .mapEvents(TopicEvent.Internal::TopicUpdatingComplete,
                    TopicEvent.Internal::TopicUpdatingError)
        }
    }
}