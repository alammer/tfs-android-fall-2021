package com.example.tfs.ui.topic.elm


import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer

class TopicReducer :
    ScreenDslReducer<TopicEvent, TopicEvent.Ui, TopicEvent.Internal, TopicState, TopicEffect, Command>(
        TopicEvent.Ui::class,
        TopicEvent.Internal::class
    ) {

    override fun Result.internal(event: TopicEvent.Internal) = when (event) {
        is TopicEvent.Internal.LocalTopicLoadingComplete -> {
            if (event.uiTopic.itemList.isNotEmpty()) {
                state {
                    copy(
                        topicList = event.uiTopic.itemList,
                        upAnchor = event.uiTopic.upAnchorId,
                        downAnchor = event.uiTopic.downAnchorId
                    )
                }
            }
            commands {
                +Command.GetRemoteTopic(
                    initialState.streamName,
                    initialState.topicName
                )
            }
        }

        is TopicEvent.Internal.RemoteTopicLoadingComplete -> {
            if (event.uiTopic.itemList.isNotEmpty()) {
                state {
                    copy(
                        isEmptyData = false,
                        isLoading = false,
                        topicList = event.uiTopic.itemList,
                        upAnchor = event.uiTopic.upAnchorId,
                        downAnchor = event.uiTopic.downAnchorId
                    )
                }
            } else {
                state {
                    copy(
                        isEmptyData = true,
                        isLoading = false,
                        topicList = event.uiTopic.itemList,
                        upAnchor = event.uiTopic.upAnchorId,
                        downAnchor = event.uiTopic.downAnchorId
                    )
                }
            }
        }

        is TopicEvent.Internal.LocalTopicLoadingError -> {
            effects { +TopicEffect.LoadTopicError(event.error) }
        }

        is TopicEvent.Internal.RemoteTopicLoadingError -> {
            state { copy(isLoading = false) }
            effects { +TopicEffect.LoadTopicError(event.error) }
        }

        is TopicEvent.Internal.PageUploadingComplete -> {
            state {
                copy(
                    topicList = event.uiTopic.itemList,
                    upAnchor = event.uiTopic.upAnchorId,
                    downAnchor = event.uiTopic.downAnchorId,
                    isPageUploading = false
                )
            }
        }

        is TopicEvent.Internal.PageUploadingError -> {
            state { copy(isPageUploading = false) }
            effects { +TopicEffect.PageUploadError(event.error) }
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

    override fun Result.ui(
        event: TopicEvent.Ui
    ) = when (event) {
        is TopicEvent.Ui.Init -> {
            state { copy(isLoading = true, error = null) }
            commands {
                +Command.FetchLocalTopic(
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
                    state.streamName,
                    state.topicName,
                    event.postId,
                    event.emojiName,
                    event.emojiCode
                )
            }
        }

        is TopicEvent.Ui.PostTapped -> {
            if (state.isLoading.not()) {
                effects { +TopicEffect.PostEditDialog(event.postId, event.isOwner) }
            } else {
                Any()
            }
        }

        is TopicEvent.Ui.NewReactionPicked -> {
            state { copy(error = null) }
            commands {
                +Command.UpdatePostReaction(
                    state.streamName,
                    state.topicName,
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
            commands {
                +Command.SendPost(
                    state.streamName,
                    state.topicName,
                    state.messageDraft
                )
            }
            state { copy(messageDraft = "") }
            effects { +TopicEffect.PostSend }
        }

        is TopicEvent.Ui.PageUploading -> {
            if (state.isPageUploading.not()) {
                if (event.isDownScroll) {
                    state { copy(error = null, isPageUploading = true) }
                    commands {
                        +Command.FetchNextPage(
                            state.streamName,
                            state.topicName,
                            state.downAnchor
                        )
                    }
                } else {
                    state { copy(error = null, isPageUploading = true) }
                    commands {
                        +Command.FetchPreviousPage(
                            state.streamName,
                            state.topicName,
                            state.upAnchor
                        )
                    }
                }
            } else {
                Any()
            }
        }

        is TopicEvent.Ui.PostMoving -> {
            Any()
        }

        is TopicEvent.Ui.PostEditing -> {
            Any()
        }

        is TopicEvent.Ui.PostCopying -> {
            Any()
        }

        is TopicEvent.Ui.PostDeleting -> {
            Any()
        }
    }
}