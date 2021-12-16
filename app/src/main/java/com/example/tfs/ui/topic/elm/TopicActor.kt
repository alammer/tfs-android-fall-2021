package com.example.tfs.ui.topic.elm

import com.example.tfs.domain.topic.PostInteractor
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat


class TopicActor /*@Inject constructor*/(
    private val postInteractor: PostInteractor,
) :
    ActorCompat<Command, TopicEvent.Internal> {
    override fun execute(command: Command): Observable<TopicEvent.Internal> = when (command) {
        is Command.FetchRecentPostList -> {
            postInteractor.fetchRecentPostList(command.streamName, command.topicName)
                .mapEvents(TopicEvent.Internal::TopicLoadingComplete,
                    TopicEvent.Internal::TopicLoadingError)
        }
        is Command.FetchNextPagePostList -> {
            postInteractor.fetchNextPage(command.streamName, command.topicName, command.downAnchor)
                .mapEvents(TopicEvent.Internal::TopicUpdatingComplete,
                    TopicEvent.Internal::TopicUpdatingError)
        }
        is Command.FetchPreviousPagePostList -> {
            postInteractor.fetchPreviousPage(command.streamName, command.topicName, command.upAnchor)
                .mapEvents(TopicEvent.Internal::TopicUpdatingComplete,
                    TopicEvent.Internal::TopicUpdatingError)
        }
        is Command.SendPost -> {
            postInteractor.sendPost(command.streamName, command.topicName, command.message)
                .mapEvents(TopicEvent.Internal::TopicUpdatingComplete,
                    TopicEvent.Internal::TopicUpdatingError)
        }
        is Command.UpdatePostReaction -> {
            postInteractor.updatePost(command.postId, command.emojiName, command.emojiCode)
                .mapEvents(TopicEvent.Internal::TopicUpdatingComplete,
                    TopicEvent.Internal::TopicUpdatingError)
        }
    }
}