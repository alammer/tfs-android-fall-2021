package com.example.tfs.ui.topic.elm

import android.util.Log
import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer

class TopicReducer :
    ScreenDslReducer<TopicEvent, TopicEvent.Ui, TopicEvent.Internal, TopicState, TopicEffect, Command>(
        TopicEvent.Ui::class,
        TopicEvent.Internal::class) {

    override fun Result.internal(event: TopicEvent.Internal) = when (event) {
        is TopicEvent.Internal.TopicLoadingComplete -> {
            Log.i("TopicReducer", "Function called: first load complete")
            state {
                copy(isLoading = false,
                    isNewestPage = true,
                    topicList = event.uiTopic.itemList,
                    upAnchor = event.uiTopic.upAnchorId,
                    downAnchor = event.uiTopic.downAnchorId)
            }
        }
        is TopicEvent.Internal.TopicLoadingError -> {
            state { copy(isLoading = false) }
            effects { +TopicEffect.LoadError(event.error) }
        }
        is TopicEvent.Internal.TopicUpdatingComplete -> {
            state {
                copy(
                    isNewestPage = false,
                    isNextPageLoading = false,
                    isPrevPageLoading = false,
                    topicList = event.uiTopic.itemList,
                    upAnchor = event.uiTopic.upAnchorId,
                    downAnchor = event.uiTopic.downAnchorId
                )
            }
        }
        is TopicEvent.Internal.TopicUpdatingError -> {
            state { copy(isNextPageLoading = false, isPrevPageLoading = false) }
            effects { +TopicEffect.UpdateError(event.error) }
        }
        is TopicEvent.Internal.MessageSendingComplete -> {
            state {
                copy(
                    messageDraft = "",
                    isNewestPage = true,
                    isNextPageLoading = false,
                    isPrevPageLoading = false,
                    topicList = event.uiTopic.itemList,
                    upAnchor = event.uiTopic.upAnchorId,
                    downAnchor = event.uiTopic.downAnchorId
                )
            }
        }
    }

    override fun Result.ui(event: TopicEvent.Ui) = when (event) {

        is TopicEvent.Ui.Init -> {
            Log.i("TopicReducer", "Function called: init")
            state { copy(isLoading = true, error = null) }
        }
        is TopicEvent.Ui.InitialLoad -> {
            Log.i("TopicReducer", "Function called: first load start")
            state { copy(streamName = event.streamName, topicName = event.topicName) }
            commands { +Command.FetchTopic(event.streamName, event.topicName) }
        }
        is TopicEvent.Ui.BackToStream -> {
            commands { +Command.BackToStream }
        }
        is TopicEvent.Ui.ReactionClicked -> {
            state { copy(error = null) }
            commands {
                +Command.UpdateReaction(event.postId,
                    event.emojiName,
                    event.emojiCode)
            }
        }
        is TopicEvent.Ui.NewReactionAdding -> {
            commands { +Command.AddReaction(event.postId) }
        }
        is TopicEvent.Ui.NewReactionPicked -> {
            state { copy(error = null) }
            commands {
                +Command.UpdateReaction(event.postId,
                    event.emojiName,
                    event.emojiCode)
            }
        }
        is TopicEvent.Ui.MessageDraftChanging -> {
            state { copy(messageDraft = event.draft) }
            effects { +TopicEffect.MessageDraftChange(event.draft) }
        }
        is TopicEvent.Ui.MessageSending -> {
            state { copy(isNextPageLoading = true, isPrevPageLoading = false, error = null) }
            commands { +Command.SendMessage(state.streamName, state.topicName, state.messageDraft) }
            effects { +TopicEffect.MessageSend }
        }
        is TopicEvent.Ui.PageFetching -> {
            Log.i("TopicReducer", "Function called: page fetching")
            state {
                copy(isNextPageLoading = event.isDownScroll, isPrevPageLoading = event.isDownScroll.not(), error = null)
            }
            if (event.isDownScroll) {
                commands {
                    +Command.FetchNextPage(state.streamName,
                        state.topicName,
                        state.downAnchor)
                }
            } else{
                commands {
                    +Command.FetchPrevPage(state.streamName,
                        state.topicName,
                        state.upAnchor)
                }
            }
        }
    }
}