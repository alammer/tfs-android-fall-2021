package com.example.tfs.ui.topic.elm


import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer

class TopicReducer :
    ScreenDslReducer<TopicEvent, TopicEvent.Ui, TopicEvent.Internal, TopicState, TopicEffect, Command>(
        TopicEvent.Ui::class,
        TopicEvent.Internal::class
    ) {

    override fun Result.internal(event: TopicEvent.Internal) = when (event) {
        is TopicEvent.Internal.TopicLoadingComplete -> {
            state {
                copy(
                    isLoading = false,
                    topicList = event.uiTopic.itemList,
                    upAnchor = event.uiTopic.upAnchorId,
                    downAnchor = event.uiTopic.downAnchorId
                )
            }
        }

        is TopicEvent.Internal.TopicLoadingError -> {
            state { copy(isLoading = false) }
            effects { +TopicEffect.LoadTopicError(event.error) }
        }

        is TopicEvent.Internal.PostListUploadingComplete -> {
            state {
                copy(
                    topicList = event.uiTopic.itemList,
                    upAnchor = event.uiTopic.upAnchorId,
                    downAnchor = event.uiTopic.downAnchorId
                )
            }
        }

        is TopicEvent.Internal.PostListUploadingError -> {
            effects { +TopicEffect.PostListUploadError(event.error) }
        }

        is TopicEvent.Internal.TopicUpdatingComplete -> {
            state {
                copy(
                    topicList = event.uiTopic.itemList,
                    upAnchor = event.uiTopic.upAnchorId,
                    downAnchor = event.uiTopic.downAnchorId
                )
            }
        }

        is TopicEvent.Internal.TopicUpdatingError -> {
            state { copy() }
            effects { +TopicEffect.UpdateTopicError(event.error) }
        }

        is TopicEvent.Internal.PostSendingComplete -> {
            state {
                copy(
                    topicList = event.uiTopic.itemList,
                    upAnchor = event.uiTopic.upAnchorId,
                    downAnchor = event.uiTopic.downAnchorId
                )
            }
        }

        is TopicEvent.Internal.PostSendingError -> {
            state {
                copy()
            }
            effects { +TopicEffect.UpdateTopicError(event.error) }
        }
    }

    override fun Result.ui(event: TopicEvent.Ui) = when (event) {

        is TopicEvent.Ui.Init -> {
            state { copy(isLoading = true, error = null) }
            commands {
                +Command.FetchRecentPostList(
                    initialState.streamName,
                    initialState.topicName
                )
            }
        }

        is TopicEvent.Ui.BackToStream -> {
            state { copy(error = null, isLoading = false) }
            effects { +TopicEffect.BackNavigation }
        }

        is TopicEvent.Ui.NewReactionAdding -> {
            effects { +TopicEffect.AddReactionDialog(event.postId) }
        }

        is TopicEvent.Ui.ReactionClicked -> {
            state { copy(error = null) }
            commands {
                +Command.UpdatePostReaction(
                    event.postId,
                    event.emojiName,
                    event.emojiCode
                )
            }
        }

        is TopicEvent.Ui.NewReactionPicked -> {
            state { copy(error = null) }
            commands {
                +Command.UpdatePostReaction(
                    event.postId,
                    event.emojiName,
                    event.emojiCode
                )
            }
        }

        is TopicEvent.Ui.PostDraftChanging -> {
            state { copy(messageDraft = event.draft) }
            effects { +TopicEffect.MessageDraftChange(event.draft) }
        }

        is TopicEvent.Ui.PostSending -> {
            state { copy(error = null) }
            commands { +Command.SendPost(state.streamName, state.topicName, state.messageDraft) }
            state { copy(messageDraft = "") }
            effects { +TopicEffect.PostSend }
        }

        is TopicEvent.Ui.PostListUploading -> {
            if (event.isDownScroll) {
                state { copy(error = null) }
                commands {
                    +Command.FetchNextPagePostList(
                        state.streamName,
                        state.topicName,
                        state.downAnchor
                    )
                }
            } else {
                if (event.isDownScroll.not()) {
                    state { copy(error = null) }
                    commands {
                        +Command.FetchPreviousPagePostList(
                            state.streamName,
                            state.topicName,
                            state.upAnchor
                        )
                    }
                } else {
                    Any()
                }
            }
        }
    }
}