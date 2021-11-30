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
                    topicList = event.uiTopic.itemList,
                    upAnchor = event.uiTopic.upAnchorId,
                    downAnchor = event.uiTopic.downAnchorId)
            }
            effects { +TopicEffect.LoadTopic }
        }
        is TopicEvent.Internal.TopicLoadingError -> {
            state { copy(isLoading = false) }
            effects { +TopicEffect.LoadError(event.error) }
        }
        is TopicEvent.Internal.TopicUpdatingComplete -> {
            state {
                copy(
                    isNextPageLoading = false,
                    isPrevPageLoading = false,
                    topicList = event.uiTopic.itemList,
                    upAnchor = event.uiTopic.upAnchorId,
                    downAnchor = event.uiTopic.downAnchorId
                )
            }
            effects { +TopicEffect.UpdateTopic }
        }
        is TopicEvent.Internal.TopicUpdatingError -> {
            state { copy(isNextPageLoading = false, isPrevPageLoading = false) }
            effects { +TopicEffect.UpdateError(event.error) }
        }
        is TopicEvent.Internal.MessageSendingComplete -> {
            state {
                copy(
                    isNextPageLoading = false,
                    isPrevPageLoading = false,
                    topicList = event.uiTopic.itemList,
                    upAnchor = event.uiTopic.upAnchorId,
                    downAnchor = event.uiTopic.downAnchorId
                )
            }
            effects { +TopicEffect.LoadTopic }
        }
        is TopicEvent.Internal.MessageSendingError -> {
            state {
                copy(
                    isNextPageLoading = false,
                    isPrevPageLoading = false,
                )
            }
            effects { +TopicEffect.LoadTopic }
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
            state {
                copy(messageDraft = "",
                    isNextPageLoading = true,
                    isPrevPageLoading = false,
                    error = null)
            }
            commands { +Command.SendMessage(state.streamName, state.topicName, state.messageDraft) }
            effects { +TopicEffect.MessageSend }
        }
        is TopicEvent.Ui.PageFetching -> {
            //Log.i("TopicReducer", "Function called: page fetching")
            if (event.isDownScroll && state.isNextPageLoading.not()) {
                state {
                    copy(isNextPageLoading = true, isPrevPageLoading = false, error = null)
                }
                commands {
                    +Command.FetchNextPage(state.streamName,
                        state.topicName,
                        state.downAnchor)
                }
            } else {
                if (event.isDownScroll.not() && state.isPrevPageLoading.not()) {
                    state {
                        copy(isNextPageLoading = false, isPrevPageLoading = true, error = null)
                    }
                    commands {
                        +Command.FetchPrevPage(state.streamName,
                            state.topicName,
                            state.upAnchor)
                    }
                } else {
                    Any()
                }
            }
        }
    }
}