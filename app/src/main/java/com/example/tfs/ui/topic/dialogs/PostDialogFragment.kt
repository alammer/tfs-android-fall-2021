package com.example.tfs.ui.topic.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import com.example.tfs.R
import com.example.tfs.ui.topic.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class PostDialogFragment : BottomSheetDialogFragment() {

    private val isOwner by lazy {
        requireArguments().getBoolean(OWNER_KEY, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.post_bsd_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val changeReaction: TextView = view.findViewById(R.id.tvPostEmoji)
        val movePost: TextView = view.findViewById(R.id.tvPostMove)
        val editPost: TextView = view.findViewById(R.id.tvPostEdit)
        val copyPost: TextView = view.findViewById(R.id.tvPostCopy)
        val deletePost: TextView = view.findViewById(R.id.tvPostDelete)

        val onClickListener =
            View.OnClickListener { item ->
                when (item.id) {
                    R.id.tvPostEmoji -> returnDialogPick(ADD_REACTION)
                    R.id.tvPostMove -> returnDialogPick(MOVE_POST)
                    R.id.tvPostEdit -> returnDialogPick(EDIT_POST)
                    R.id.tvPostCopy -> returnDialogPick(COPY_POST)
                    R.id.tvPostDelete -> returnDialogPick(DELETE_POST)
                }
            }

        changeReaction.isVisible= isOwner.not()
        movePost.isVisible = isOwner
        editPost.isVisible = isOwner
        deletePost.isVisible = isOwner

        changeReaction.setOnClickListener(onClickListener)
        movePost.setOnClickListener(onClickListener)
        editPost.setOnClickListener(onClickListener)
        copyPost.setOnClickListener(onClickListener)
        deletePost.setOnClickListener(onClickListener)
    }

    private fun returnDialogPick(pick: Int) {
        val response = bundleOf(
            POST_RESPONSE_ID to requireArguments().getInt(POST_KEY, -1),
            POST_RESPONSE_PICK to pick
        )
        setFragmentResult(
            POST_REQUEST_KEY,
            bundleOf(POST_RESPONCE_KEY to response),
        )
        dismiss()
    }


    companion object {

        private const val POST_KEY = "post_id"
        private const val OWNER_KEY = "is_owner"

        fun newInstance(
            postId: Int,
            isOwner: Boolean
        ): PostDialogFragment {
            val fragment = PostDialogFragment()
            val arguments = Bundle()
            arguments.putInt(POST_KEY, postId)
            arguments.putBoolean(OWNER_KEY, isOwner)
            fragment.arguments = arguments
            return fragment
        }
    }
}

const val ADD_REACTION = 100
const val MOVE_POST = 200
const val EDIT_POST = 300
const val COPY_POST = 400
const val DELETE_POST = 500