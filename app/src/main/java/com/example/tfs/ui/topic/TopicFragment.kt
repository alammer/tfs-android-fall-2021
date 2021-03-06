package com.example.tfs.ui.topic

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.appComponent
import com.example.tfs.common.baseitems.BaseLoader
import com.example.tfs.common.baseitems.LoaderItem
import com.example.tfs.common.baseitems.TextShimmerItem
import com.example.tfs.databinding.FragmentTopicBinding
import com.example.tfs.di.DaggerTopicComponent
import com.example.tfs.ui.stream.*
import com.example.tfs.ui.topic.adapter.TopicAdapter
import com.example.tfs.ui.topic.adapter.decorations.ItemDateDecorator
import com.example.tfs.ui.topic.adapter.decorations.ItemPostDecorator
import com.example.tfs.ui.topic.adapter.items.DateItem
import com.example.tfs.ui.topic.adapter.items.OwnerPostItem
import com.example.tfs.ui.topic.adapter.items.UserPostItem
import com.example.tfs.ui.topic.dialogs.*
import com.example.tfs.ui.topic.dialogs.add_emoji_bsd.EmojiDialogFragment
import com.example.tfs.ui.topic.elm.*
import com.example.tfs.util.hideSoftKeyboard
import com.example.tfs.util.showSnackbarError
import com.example.tfs.util.toPx
import com.example.tfs.util.viewbinding.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import vivid.money.elmslie.storepersisting.retainStoreHolder
import javax.inject.Inject


class TopicFragment : ElmFragment<TopicEvent, TopicEffect, TopicState>(R.layout.fragment_topic) {

    override val initEvent: TopicEvent = TopicEvent.Ui.Init

    lateinit var clipboardManager: ClipboardManager

    @Inject
    lateinit var topicActor: TopicActor

    private val topicName by lazy {
        requireArguments().getString(TOPIC_NAME, "")
    }

    private val streamName by lazy {
        requireArguments().getString(STREAM_NAME, "")
    }

    private val streamId by lazy {
        requireArguments().getInt(STREAM_ID, -1)
    }

    private val viewBinding by viewBinding(FragmentTopicBinding::bind)

    private val topicAdapter = TopicAdapter(getItemTypes())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.setFragmentResultListener(EMOJI_REQUEST_KEY, this) { _, bundle ->
            bundle.getBundle(EMOJI_RESPONSE_KEY)?.let { response ->
                val updatedPostId = response.getInt(EMOJI_RESPONSE_POST)
                val updatedEmojiName =
                    response.getString(EMOJI_RESPONSE_NAME) ?: return@setFragmentResultListener
                val updatedEmojiCode =
                    response.getString(EMOJI_RESPONSE_CODE) ?: return@setFragmentResultListener
                store.accept(
                    TopicEvent.Ui.NewReactionPick(
                        updatedPostId,
                        updatedEmojiName,
                        updatedEmojiCode
                    )
                )
            }
        }
        childFragmentManager.setFragmentResultListener(POST_REQUEST_KEY, this) { _, bundle ->

            bundle.getBundle(POST_RESPONCE_KEY)?.let { response ->
                val updatedPostId = response.getInt(POST_RESPONSE_ID)
                when (response.getInt(POST_RESPONSE_PICK)) {
                    ADD_REACTION -> store.accept(TopicEvent.Ui.NewReactionAdding(updatedPostId))
                    MOVE_POST -> store.accept(TopicEvent.Ui.ChangeTopicForPostPick(updatedPostId))
                    EDIT_POST -> store.accept(TopicEvent.Ui.PostEditPick(updatedPostId))
                    COPY_POST -> store.accept(TopicEvent.Ui.PostCopyPick(updatedPostId))
                    DELETE_POST -> store.accept(TopicEvent.Ui.PostDeletePick(updatedPostId))
                    else -> return@setFragmentResultListener
                }
            }
        }
    }

    override fun createStore(): Store<TopicEvent, TopicEffect, TopicState> =
        TopicStore.provide(
            TopicState(
                topicName = topicName,
                streamName = streamName,
                streamId = streamId
            ), topicActor
        )

    override val storeHolder by retainStoreHolder(storeProvider = ::createStore)

    override fun render(state: TopicState) {
        with(viewBinding) {
            loading.root.isVisible = state.isLoading
            empty.root.isVisible = state.isEmptyData
            lEditLayout.isVisible = state.isEditMode
        }
        topicAdapter.uploadData(state.topicList)
    }

    override fun handleEffect(effect: TopicEffect) {
        when (effect) {
            is TopicEffect.BackNavigation -> {
                requireActivity().supportFragmentManager.popBackStack()
            }
            is TopicEffect.UpdateTopicError -> {
                with(requireView()) {
                    effect.error.message?.let { showSnackbarError(it) }
                        ?: showSnackbarError("Error on update topic")
                }
            }
            is TopicEffect.LoadTopicError -> {
                with(requireView()) {
                    effect.error.message?.let { showSnackbarError(it) }
                        ?: showSnackbarError("Error on load topic")
                }
            }
            is TopicEffect.PageUploadError -> {
                with(requireView()) {
                    effect.error.message?.let { showSnackbarError(it) }
                        ?: showSnackbarError("Error on upload post list")
                }
            }
            is TopicEffect.MessageDraftChange -> {
                viewBinding.btnSendPost.setImageResource(if (effect.draft.isBlank()) R.drawable.ic_text_plus else R.drawable.ic_send_arrow)
            }
            is TopicEffect.PostSend -> {
                viewBinding.btnSendPost.setImageResource(R.drawable.ic_text_plus)
                viewBinding.etMessage.apply {
                    text.clear()
                    clearFocus()
                }
            }
            is TopicEffect.ShowNewPost -> {
                scrollToLast(effect.topicSize)
            }
            is TopicEffect.PostCopy -> {
                copyText(effect.message)
            }
            is TopicEffect.PostEdit -> {
                editPost(effect.post.content)
            }
            is TopicEffect.TopicChange -> {
                changeTopic(effect.topicList)
            }
            is TopicEffect.PostNotFound -> {
                with(requireView()) {
                    showSnackbarError("Can't find requested post!")
                }
            }
            is TopicEffect.ShowPostDialog -> {
                PostDialogFragment.newInstance(effect.postId, effect.isOwner)
                    .show(childFragmentManager, tag)
            }
            is TopicEffect.ShowReactionDialog -> {
                EmojiDialogFragment.newInstance(effect.postId).show(childFragmentManager, tag)
            }
        }
    }

    override fun onAttach(context: Context) {
        DaggerTopicComponent.builder().appComponent(context.appComponent).build()
            .inject(this)
        super.onAttach(context)
    }

    override fun onDestroyView() {
        viewBinding.rvTopic.clearOnScrollListeners() //TODO("???")
        super.onDestroyView()
    }

    private fun getItemTypes() = listOf(
        OwnerPostItem(::tapOnPost),
        UserPostItem(::updateReaction, ::addReaction, ::tapOnPost),
        DateItem(),
        LoaderItem(),
        TextShimmerItem()
    )

    private fun initViews() {
        with(viewBinding) {
            tvTopic.text = root.context.getString(
                R.string.topic_name_template,
                topicName
            )
            tvStream.text = streamName

            with(rvTopic) {
                val adapterLayoutManager = LinearLayoutManager(context)

                setHasFixedSize(true)

                topicAdapter.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                adapter = topicAdapter

                layoutManager = adapterLayoutManager

                addOnScrollListener(object :
                    TopicScrollListetner(adapterLayoutManager) { //TODO remove in onDestroyView()
                    override fun loadPage(isDownScroll: Boolean) {
                        store.accept(TopicEvent.Ui.PageUploading(isDownScroll))
                        if (isDownScroll) {
                            topicAdapter.addFooterItem(BaseLoader)
                        } else {
                            topicAdapter.addHeaderItem(BaseLoader)
                        }
                    }
                })
                addItemDecoration(
                    ItemDateDecorator(
                        R.layout.item_post_date,
                        DATE_ITEM_DIVIDER.toPx,
                    )
                )
                addItemDecoration(
                    ItemPostDecorator(
                        viewType = R.layout.item_post_owner,
                        verticalDivider = POST_ITEM_DIVIDER.toPx,
                        endPadding = OWNER_POST_ITEM_END_PADDING.toPx
                    )
                )
                addItemDecoration(
                    ItemPostDecorator(
                        viewType = R.layout.item_post,
                        verticalDivider = POST_ITEM_DIVIDER.toPx,
                        startPadding = USER_POST_ITEM_START_PADDING.toPx,
                        endPadding = USER_POST_ITEM_END_PADDING.toPx,
                    )
                )
            }

            imgEditCancel.setOnClickListener {
                store.accept(TopicEvent.Ui.PostEditCancel)
            }

            imgEditComplete.setOnClickListener {
                store.accept(TopicEvent.Ui.PostEditComplete(etPostBody.text.toString()))
            }

            btnSendPost.setOnClickListener {
                if (etMessage.text.isNotBlank()) {
                    store.accept(TopicEvent.Ui.NewPostSending)
                }
                requireActivity().currentFocus?.apply { hideSoftKeyboard() }
            }

            btnTopicNavBack.setOnClickListener {
                store.accept(TopicEvent.Ui.BackToStream)
            }

            etMessage.doAfterTextChanged {
                store.accept(TopicEvent.Ui.PostDraftChanging(it.toString()))
            }
        }
    }

    private fun tapOnPost(postId: Int, isOwner: Boolean) {
        store.accept(TopicEvent.Ui.PostTapped(postId, isOwner))
    }

    private fun scrollToLast(lastPositon: Int) {
        viewBinding.rvTopic.smoothScrollToPosition(lastPositon - 1) //TODO("AdapterDataObserver")
    }

    private fun editPost(content: String) {
        with(viewBinding) {
            tvEditPost.text = content
            etPostBody.setText(content)
        }
    }

    private fun addReaction(postId: Int) {
        store.accept(TopicEvent.Ui.NewReactionAdding(postId))
    }

    private fun updateReaction(postId: Int, emojiName: String, emojiCode: String) {
        store.accept(TopicEvent.Ui.ReactionClicked(postId, emojiName, emojiCode))
    }

    private fun changeTopic(topicList: List<String>) {
        val topics = topicList.toTypedArray()
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("${resources.getString(R.string.select_topic_dialog)} #$streamName")
            .setItems(topics) { _, which ->
                store.accept(TopicEvent.Ui.NewTopicForPostPick(topics[which]))
            }
            .show()
    }

    private fun copyText(message: String) {
        clipboardManager =
            activity?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("key", message)
        clipboardManager.setPrimaryClip(clipData)
    }

    companion object {

        private const val STREAM_ID = "stream_id"
        private const val TOPIC_NAME = "topic_name"
        private const val STREAM_NAME = "stream_name"

        fun newInstance(
            topicName: String,
            streamName: String,
            streamId: Int,
        ): TopicFragment {
            return TopicFragment().apply {
                arguments = bundleOf(
                    TOPIC_NAME to topicName,
                    STREAM_NAME to streamName,
                    STREAM_ID to streamId
                )
            }
        }
    }
}

const val EMOJI_REQUEST_KEY = "emoji_request"
const val EMOJI_RESPONSE_KEY = "emoji_response"
const val EMOJI_RESPONSE_POST = "emoji_key"
const val EMOJI_RESPONSE_NAME = "emoji_name"
const val EMOJI_RESPONSE_CODE = "emoji_id"

const val POST_RESPONCE_KEY = "post_bsd_response"
const val POST_RESPONSE_ID = "post_key"
const val POST_RESPONSE_PICK = "post_bsd_pick"
const val POST_REQUEST_KEY = "post_bsd_request"

private const val DATE_ITEM_DIVIDER = 4
private const val POST_ITEM_DIVIDER = 16
private const val OWNER_POST_ITEM_END_PADDING = 12
private const val USER_POST_ITEM_START_PADDING = 12
private const val USER_POST_ITEM_END_PADDING = 80

