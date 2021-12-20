package com.example.tfs.ui.topic.elm

import com.example.tfs.domain.topic.PostInteractor
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat


class TopicActor /*@Inject constructor*/(
    private val postInteractor: PostInteractor,
) :
    ActorCompat<Command, TopicEvent.Internal> {
    override fun execute(command: Command): Observable<TopicEvent.Internal> = when (command) {
        is Command.FetchLocalTopic -> {
            postInteractor.fetchLocalTopic(command.streamName, command.topicName)
                .mapEvents(TopicEvent.Internal::LocalTopicLoadingComplete,
                    TopicEvent.Internal::LocalTopicLoadingError)
        }
        is Command.GetRemoteTopic -> {
            postInteractor.getRemoteTopic(command.streamName, command.topicName)
                .mapEvents(TopicEvent.Internal::RemoteTopicLoadingComplete,
                    TopicEvent.Internal::RemoteTopicLoadingError)
        }
        is Command.FetchNextPage -> {
            postInteractor.fetchNextPage(command.streamName, command.topicName, command.downAnchor)
                .mapEvents(TopicEvent.Internal::PageUploadingComplete,
                    TopicEvent.Internal::PageUploadingError)
        }
        is Command.FetchPreviousPage -> {
            postInteractor.fetchPreviousPage(command.streamName, command.topicName, command.upAnchor)
                .mapEvents(TopicEvent.Internal::PageUploadingComplete,
                    TopicEvent.Internal::PageUploadingError)
        }
        is Command.SendPost -> {
            postInteractor.sendPost(command.streamName, command.topicName, command.message)
                .mapEvents(TopicEvent.Internal::TopicUpdatingComplete,
                    TopicEvent.Internal::TopicUpdatingError)
        }
        is Command.DeletePost -> {
            postInteractor.deletePost(command.streamName, command.topicName, command.postId)
                .mapEvents(TopicEvent.Internal::TopicUpdatingComplete,
                    TopicEvent.Internal::TopicUpdatingError)
        }
        is Command.CopyPost -> {
            postInteractor.getPost(command.postId)
                .mapEvents(TopicEvent.Internal::GetPostForCopyComplete,
                    TopicEvent.Internal.GetPostComplition,
                    TopicEvent.Internal::GetPostError)
        }
        is Command.EditPost -> {
            postInteractor.getPost(command.postId)
                .mapEvents(TopicEvent.Internal::GetPostForEditComplete,
                    TopicEvent.Internal.GetPostComplition,
                    TopicEvent.Internal::GetPostError)
        }
        is Command.GetTopicList -> {
            postInteractor.getTopicList(command.streamId)
                .mapEvents(TopicEvent.Internal::GetTopicListComplete,
                    TopicEvent.Internal::GetTopicListError)
        }
        is Command.MovePost -> {
            postInteractor.getPost(command.postId)
                .mapEvents(TopicEvent.Internal::GetPostForEditComplete,
                    TopicEvent.Internal.GetPostComplition,
                    TopicEvent.Internal::GetPostError)
        }
        is Command.UpdatePostReaction -> {
            postInteractor.updatePost(command.streamName, command.topicName, command.postId, command.emojiName, command.emojiCode)
                .mapEvents(TopicEvent.Internal::TopicUpdatingComplete,
                    TopicEvent.Internal::TopicUpdatingError)
        }
    }
}