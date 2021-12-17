package com.example.tfs.ui.topic.elm

import com.example.tfs.common.baseadapter.AdapterItem
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
)

sealed class TopicEvent {

    sealed class Ui : TopicEvent() {

        object Init : Ui()

        object BackToStream : Ui()

        data class ReactionClicked(val postId: Int, val emojiName: String, val emojiCode: String) :
            Ui()

        data class NewReactionAdding(val postId: Int) : Ui()

        data class NewReactionPicked(
            val postId: Int,
            val emojiName: String,
            val emojiCode: String
        ) : Ui()

        data class PostDraftChanging(val draft: String) : Ui()

        object PostSending : Ui()

        data class PageUploading(val isDownScroll: Boolean) : Ui()
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
    }
}

sealed class TopicEffect {

    data class LoadTopicError(val error: Throwable) : TopicEffect()

    data class PageUploadError(val error: Throwable) : TopicEffect()

    data class UpdateTopicError(val error: Throwable) : TopicEffect()

    object PostSend : TopicEffect()

    object BackNavigation : TopicEffect()

    data class AddReactionDialog(val postId: Int) : TopicEffect()

    data class MessageDraftChange(val draft: String) : TopicEffect()
}

sealed class Command {

    data class FetchLocalTopic(val streamName: String, val topicName: String) : Command()

    data class GetRemoteTopic(val streamName: String, val topicName: String) : Command()

    data class UpdatePostReaction(val streamName: String, val topicName: String, val postId: Int, val emojiName: String, val emojiCode: String) :
        Command()

    data class SendPost(val streamName: String, val topicName: String, val message: String) :
        Command()

    data class FetchNextPage(val streamName: String, val topicName: String, val downAnchor: Int) :
        Command()

    data class FetchPreviousPage(val streamName: String, val topicName: String, val upAnchor: Int) :
        Command()
}