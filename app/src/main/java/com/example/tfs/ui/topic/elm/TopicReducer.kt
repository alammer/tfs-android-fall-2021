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
                +Command.FetchRemoteTopic(
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
            if (event.uiTopic.itemList.isNotEmpty()) {
                state {
                    copy(
                        topicList = event.uiTopic.itemList,
                        upAnchor = event.uiTopic.upAnchorId,
                        downAnchor = event.uiTopic.downAnchorId,
                        isLoading = false

                    )
                }
            } else {
                state {
                    copy(
                        isEmptyData = true,
                        isLoading = false,
                    )
                }
            }
        }

        is TopicEvent.Internal.TopicUpdatingError -> {
            state { copy(isLoading = false) }
            effects { +TopicEffect.UpdateTopicError(event.error) }
        }

        is TopicEvent.Internal.NewPostAccept -> {
            state {
                copy(
                    topicList = event.uiTopic.itemList,
                    upAnchor = event.uiTopic.upAnchorId,
                    downAnchor = event.uiTopic.downAnchorId,
                    isLoading = false
                )
            }
            effects { +TopicEffect.ShowNewPost(event.uiTopic.itemList.size) }
        }

        is TopicEvent.Internal.PostSendingError -> {
            state {
                copy(isLoading = false)
            }
            effects { +TopicEffect.UpdateTopicError(event.error) }
        }

        is TopicEvent.Internal.GetPostForCopyComplete -> {
            effects { +TopicEffect.PostCopy(event.post.content) }
        }

        is TopicEvent.Internal.GetPostForEditComplete -> {
            state { copy(isEditMode = true) }
            effects { +TopicEffect.PostEdit(event.post) }
        }

        is TopicEvent.Internal.PostNotExist -> {
            effects { +TopicEffect.PostNotFound }
        }

        is TopicEvent.Internal.GetPostError -> {
            effects { +TopicEffect.PostNotFound }
        }

        is TopicEvent.Internal.GetTopicListComplete -> {
            effects { +TopicEffect.TopicChange(event.topicList) }
        }

        is TopicEvent.Internal.GetTopicListError -> {
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
            effects { +TopicEffect.ShowReactionDialog(event.postId) }
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

        is TopicEvent.Ui.PostTapped -> {
            if (state.isLoading.not()) {
                effects { +TopicEffect.ShowPostDialog(event.postId, event.isOwner) }
            } else {
                Any()
            }
        }

        is TopicEvent.Ui.NewReactionPick -> {
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

        is TopicEvent.Ui.NewPostSending -> {
            state { copy(error = null, isLoading = true) }
            commands {
                +Command.SendNewPost(
                    state.streamName,
                    state.topicName,
                    state.messageDraft,
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

        is TopicEvent.Ui.PostEditPick -> {
            state { copy(selectedPostId = event.postId) }
            commands { +Command.GetPostForEdit(event.postId) }
        }
        is TopicEvent.Ui.PostEditComplete -> {
            state { copy(isEditMode = false) }
            commands {
                +Command.SendEditPost(
                    event.newContent,
                    state.selectedPostId,
                    state.upAnchor,
                    state.streamName,
                    state.topicName
                )
            }
        }
        is TopicEvent.Ui.PostEditCancel -> {
            state { copy(isEditMode = false) }
        }

        is TopicEvent.Ui.PostCopyPick -> {
            commands { +Command.GetPostForCopy(event.postId) }
        }

        is TopicEvent.Ui.ChangeTopicForPostPick -> {
            state { copy(selectedPostId = event.postId) }
            commands { +Command.GetTopicList(state.streamId) }
        }

        is TopicEvent.Ui.NewTopicForPostPick -> {
            state { copy(isLoading = true) }
            commands {
                +Command.ChangeTopicForPost(
                    state.streamName,
                    event.newTopicName,
                    state.selectedPostId
                )
            }
        }

        is TopicEvent.Ui.PostDeletePick -> {
            state { copy(error = null, isLoading = true) }
            commands {
                +Command.DeletePost(
                    event.postId
                )
            }
        }
    }
}