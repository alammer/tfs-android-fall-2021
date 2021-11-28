package com.example.tfs.ui.topic.elm

import com.example.tfs.domain.topic.PostItem
import com.example.tfs.domain.topic.TopicListObject

data class TopicState(
    val topicList: List<PostItem> = emptyList(),
    val error: Throwable? = null,
    val isLoading: Boolean = false,
    val isNewestPage: Boolean = false,
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

        data class InitialLoad(val streamName: String, val topicName: String) : Ui()

        object BackToStream : Ui()

        data class ReactionClicked(val postId: Int, val emojiCode: String) : Ui()

        data class NewReactionAdding(val postId: Int) : Ui()

        data class NewReactionPicked(val postId: Int, val emojiCode: String) : Ui()

        data class MessageDraftChanging(val draft: String) : Ui()

        object MessageSending : Ui()

        data class PageFetching(val isDownScroll: Boolean) : Ui()
    }

    sealed class Internal : TopicEvent() {

        data class TopicLoadingComplete(val topic: TopicListObject) : Internal()

        data class TopicLoadingError(val error: Throwable) : Internal()

        data class TopicUpdatingComplete(val topic: TopicListObject) : Internal()

        data class TopicUpdatingError(val error: Throwable) : Internal()

        data class MessageSendingComplete(val topic: TopicListObject) : Internal()

        //data class MessageSendingError(val error: Throwable) : Internal()

        //data class ReactionUpdatingError(val error: Throwable) : Internal()
    }
}

sealed class TopicEffect {

    data class LoadError(val error: Throwable) : TopicEffect()

    data class UpdateError(val error: Throwable) : TopicEffect()

    object NextPageLoad : TopicEffect()

    object PrevPageLoad : TopicEffect()

    data class MessageDraftChange(val draft: String) : TopicEffect()
}

sealed class Command {

    data class FetchTopic(val streamName: String, val topicName: String) : Command()

    data class UpdateReaction(val streamName: String, val topicName: String, val postId: Int, val emojiCode: String) : Command()

    data class AddReaction(val postId: Int) : Command()

    data class SendMessage(val streamName: String, val topicName: String, val message: String) : Command()

    data class FetchNextPage(val streamName: String, val topicName: String, val downAnchor: Int) : Command()

    data class FetchPrevPage(val streamName: String, val topicName: String, val upAnchor: Int) : Command()

    object BackToStream : Command()
}