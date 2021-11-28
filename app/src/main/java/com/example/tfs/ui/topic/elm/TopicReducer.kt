package com.example.tfs.ui.topic.elm

import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer

class TopicReducer :
    ScreenDslReducer<TopicEvent, TopicEvent.Ui, TopicEvent.Internal, TopicState, TopicEffect, Command>(
        TopicEvent.Ui::class,
        TopicEvent.Internal::class) {

    override fun Result.internal(event: TopicEvent.Internal) = when (event) {
        is TopicEvent.Internal.TopicLoadingComplete -> {
            state {
                copy(isLoading = false,
                    isNewestPage = true,
                    topicList = event.topic.itemList,
                    upAnchor = event.topic.upAnchorId,
                    downAnchor = event.topic.downAnchorId)
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
                    topicList = event.topic.itemList,
                    upAnchor = event.topic.upAnchorId,
                    downAnchor = event.topic.downAnchorId
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
                    topicList = event.topic.itemList,
                    upAnchor = event.topic.upAnchorId,
                    downAnchor = event.topic.downAnchorId
                )
            }
        }
    }

    override fun Result.ui(event: TopicEvent.Ui) = when (event) {

        is TopicEvent.Ui.Init -> {
            state { copy(isLoading = true, error = null) }
        }
        is TopicEvent.Ui.InitialLoad -> {
            state { copy(streamName = event.streamName, topicName = event.topicName) }
            commands { +Command.FetchTopic(event.streamName, event.topicName) }
        }
        is TopicEvent.Ui.BackToStream -> {
            commands { +Command.BackToStream }
        }
        is TopicEvent.Ui.ReactionClicked -> {
            state { copy(error = null) }
            commands {
                +Command.UpdateReaction(state.streamName,
                    state.topicName,
                    event.postId,
                    event.emojiCode)
            }
        }
        is TopicEvent.Ui.NewReactionAdding -> {
            commands { +Command.AddReaction(event.postId) }
        }
        is TopicEvent.Ui.NewReactionPicked -> {
            state { copy(error = null) }
            commands {
                +Command.UpdateReaction(state.streamName,
                    state.topicName,
                    event.postId,
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
        }
        is TopicEvent.Ui.PageFetching -> {
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