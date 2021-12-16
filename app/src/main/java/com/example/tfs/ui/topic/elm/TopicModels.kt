package com.example.tfs.ui.topic.elm

import com.example.tfs.common.baseadapter.AdapterItem
import com.example.tfs.domain.topic.UiTopicListObject

data class TopicState(
    val topicList: List<AdapterItem> = emptyList(),
    val error: Throwable? = null,
    val isLoading: Boolean = false,
    val isNextPageLoading: Boolean = false,
    val isPrevPageLoading: Boolean = false,
    val messageDraft: String = "",
    val downAnchor: Int = 0,
    val upAnchor: Int = 0,
    val currentRVPosition: Int = 0,
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

        data class MessageDraftChanging(val draft: String) : Ui()

        object MessageSending : Ui()

        data class PageFetching(val isDownScroll: Boolean) : Ui()
    }

    sealed class Internal : TopicEvent() {

        data class TopicLoadingComplete(val uiTopic: UiTopicListObject) : Internal()

        data class TopicLoadingError(val error: Throwable) : Internal()

        data class TopicUpdatingComplete(val uiTopic: UiTopicListObject) : Internal()

        data class TopicUpdatingError(val error: Throwable) : Internal()

        data class MessageSendingComplete(val uiTopic: UiTopicListObject) : Internal()

        data class MessageSendingError(val error: Throwable) : Internal()
    }
}

sealed class TopicEffect {

    data class LoadError(val error: Throwable) : TopicEffect()

    data class UpdateError(val error: Throwable) : TopicEffect()

    object LoadTopic : TopicEffect()

    object UpdateTopic : TopicEffect()

    object NextPageLoad : TopicEffect()

    object PrevPageLoad : TopicEffect()

    object MessageSend : TopicEffect()

    object BackNavigation : TopicEffect()

    data class AddReactionDialog(val postId: Int) : TopicEffect()

    data class MessageDraftChange(val draft: String) : TopicEffect()
}

sealed class Command {

    data class FetchTopic(val streamName: String, val topicName: String) : Command()

    data class UpdateReaction(val postId: Int, val emojiName: String, val emojiCode: String) :
        Command()

    data class SendMessage(val streamName: String, val topicName: String, val message: String) :
        Command()

    data class FetchNextPage(val streamName: String, val topicName: String, val downAnchor: Int) :
        Command()

    data class FetchPrevPage(val streamName: String, val topicName: String, val upAnchor: Int) :
        Command()
}