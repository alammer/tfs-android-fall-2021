package com.example.tfs.ui.topic.elm

import com.example.tfs.common.baseadapter.AdapterItem
import com.example.tfs.database.entity.LocalPost
import com.example.tfs.domain.topic.UiTopicListObject

data class TopicState(
    val topicList: List<AdapterItem> = emptyList(),
    val error: Throwable? = null,
    val isLoading: Boolean = false,
    val isPageUploading: Boolean = false,
    val isEmptyData: Boolean = false,
    val messageDraft: String = "",
    val downAnchor: Int = 0,
    val upAnchor: Int = 0,
    val topicName: String = "",
    val streamName: String = "",
    val streamId: Int = -1,
    val selectedPostId: Int = -1,
)

sealed class TopicEvent {

    sealed class Ui : TopicEvent() {

        object Init : Ui()

        object BackToStream : Ui()

        data class ReactionClicked(val postId: Int, val emojiName: String, val emojiCode: String) :
            Ui()

        data class PostTapped(val postId: Int, val isOwner: Boolean) : Ui()

        data class NewReactionAdding(val postId: Int) : Ui()

        data class NewReactionPicked(
            val postId: Int,
            val emojiName: String,
            val emojiCode: String
        ) : Ui()

        data class PostDraftChanging(val draft: String) : Ui()

        object PostSending : Ui()

        data class PageUploading(val isDownScroll: Boolean) : Ui()

        data class ChangeTopicForPost(val postId: Int) : Ui()
        data class PostEditing(val postId: Int) : Ui()
        data class PostCopying(val postId: Int) : Ui()
        data class PostDeleting(val postId: Int) : Ui()
        data class PostMoving(val newTopicName: String) : Ui()
    }

    sealed class Internal : TopicEvent() {

        data class LocalTopicLoadingComplete(val uiTopic: UiTopicListObject) : Internal()

        data class RemoteTopicLoadingComplete(val uiTopic: UiTopicListObject) : Internal()

        data class LocalTopicLoadingError(val error: Throwable) : Internal()

        data class RemoteTopicLoadingError(val error: Throwable) : Internal()

        data class PageUploadingComplete(val uiTopic: UiTopicListObject) : Internal()

        data class PageUploadingError(val error: Throwable) : Internal()

        data class TopicUpdatingComplete(val uiTopic: UiTopicListObject) : Internal()

        data class TopicUpdatingError(val error: Throwable) : Internal()

        data class PostSendingComplete(val uiTopic: UiTopicListObject) : Internal()

        data class PostSendingError(val error: Throwable) : Internal()

        data class GetPostForCopyComplete(val post: LocalPost) : Internal()

        data class GetPostForEditComplete(val post: LocalPost) : Internal()

        data class GetPostError(val error: Throwable) : Internal()

        object GetPostComplition : Internal()

        data class GetTopicListComplete(val topicList: List<String>) : Internal()

        data class GetTopicListError(val error: Throwable) : Internal()
    }
}

sealed class TopicEffect {

    data class LoadTopicError(val error: Throwable) : TopicEffect()

    data class PageUploadError(val error: Throwable) : TopicEffect()

    data class UpdateTopicError(val error: Throwable) : TopicEffect()

    object PostSend : TopicEffect()

    object BackNavigation : TopicEffect()

    object PostNotFound : TopicEffect()

    data class ShowPostDialog(val postId: Int, val isOwner: Boolean) : TopicEffect()

    data class ShowReactionDialog(val postId: Int) : TopicEffect()

    data class MessageDraftChange(val draft: String) : TopicEffect()

    data class PostCopy(val message: String) : TopicEffect()

    data class PostEdit(val post: LocalPost) : TopicEffect()

    data class ChangeTopic(val topicList: List<String>) : TopicEffect()
}

sealed class Command {

    data class FetchLocalTopic(val streamName: String, val topicName: String) : Command()

    data class FetchRemoteTopic(val streamName: String, val topicName: String) : Command()

    data class UpdatePostReaction(val postId: Int, val emojiName: String, val emojiCode: String) :
        Command()

    data class SendPost(val streamName: String, val topicName: String, val message: String, val downAnchor: Int) :
        Command()

    data class DeletePost(val postId: Int) :
        Command()

    data class CopyPost(val postId: Int) :
        Command()

    data class EditPost(val postId: Int) :
        Command()

    data class GetTopicList(val streamId: Int) :
        Command()

    data class MovePost(val streamName: String, val topicName: String, val postId: Int) :
        Command()

    data class FetchNextPage(val streamName: String, val topicName: String, val downAnchor: Int) :
        Command()

    data class FetchPreviousPage(val streamName: String, val topicName: String, val upAnchor: Int) :
        Command()
}