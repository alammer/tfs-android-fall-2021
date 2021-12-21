package com.example.tfs.ui.topic.elm

import com.example.tfs.domain.topic.PostInteractor
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat


class TopicActor(
    private val postInteractor: PostInteractor,
) :
    ActorCompat<Command, TopicEvent.Internal> {
    override fun execute(command: Command): Observable<TopicEvent.Internal> = when (command) {
        is Command.FetchLocalTopic -> {
            postInteractor.fetchLocalTopic(command.streamName, command.topicName)
                .mapEvents(
                    TopicEvent.Internal::LocalTopicLoadingComplete,
                    TopicEvent.Internal::LocalTopicLoadingError
                )
        }
        is Command.FetchRemoteTopic -> {
            postInteractor.fetchRemoteTopic(command.streamName, command.topicName)
                .mapEvents(
                    TopicEvent.Internal::RemoteTopicLoadingComplete,
                    TopicEvent.Internal::RemoteTopicLoadingError
                )
        }
        is Command.FetchNextPage -> {
            postInteractor.fetchNextPage(command.streamName, command.topicName, command.downAnchor)
                .mapEvents(
                    TopicEvent.Internal::PageUploadingComplete,
                    TopicEvent.Internal::PageUploadingError
                )
        }
        is Command.FetchPreviousPage -> {
            postInteractor.fetchPreviousPage(
                command.streamName,
                command.topicName,
                command.upAnchor
            )
                .mapEvents(
                    TopicEvent.Internal::PageUploadingComplete,
                    TopicEvent.Internal::PageUploadingError
                )
        }
        is Command.SendNewPost -> {
            postInteractor.sendNewPost(
                command.streamName,
                command.topicName,
                command.message,
            )
                .mapEvents(
                    TopicEvent.Internal::NewPostAccept,
                    TopicEvent.Internal::TopicUpdatingError
                )
        }
        is Command.SendEditPost -> {
            postInteractor.sendEditPost(
                command.newContent,
                command.postId,
                command.upAnchor,
                command.streamName,
                command.topicName
            )
                .mapEvents(
                    TopicEvent.Internal::TopicUpdatingComplete,
                    TopicEvent.Internal::TopicUpdatingError
                )
        }
        is Command.DeletePost -> {
            postInteractor.deletePost(command.postId)
                .mapEvents(
                    TopicEvent.Internal::TopicUpdatingComplete,
                    TopicEvent.Internal::TopicUpdatingError
                )
        }
        is Command.GetPostForCopy -> {
            postInteractor.getPost(command.postId)
                .mapEvents(
                    TopicEvent.Internal::GetPostForCopyComplete,
                    TopicEvent.Internal.PostNotExist,
                    TopicEvent.Internal::GetPostError
                )
        }
        is Command.GetPostForEdit -> {
            postInteractor.getPost(command.postId)
                .mapEvents(
                    TopicEvent.Internal::GetPostForEditComplete,
                    TopicEvent.Internal.PostNotExist,
                    TopicEvent.Internal::GetPostError
                )
        }
        is Command.GetTopicList -> {
            postInteractor.getTopicList(command.streamId)
                .mapEvents(
                    TopicEvent.Internal::GetTopicListComplete,
                    TopicEvent.Internal::GetTopicListError
                )
        }
        is Command.ChangeTopicForPost -> {
            postInteractor.movePost(command.streamName, command.topicName, command.postId)
                .mapEvents(
                    TopicEvent.Internal::TopicUpdatingComplete,
                    TopicEvent.Internal::TopicUpdatingError
                )
        }
        is Command.UpdatePostReaction -> {
            postInteractor.updatePost(command.postId, command.emojiName, command.emojiCode)
                .mapEvents(
                    TopicEvent.Internal::TopicUpdatingComplete,
                    TopicEvent.Internal::TopicUpdatingError
                )
        }
    }
}