package com.example.tfs.presentation.topic.emoji

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.presentation.topic.TOPIC_REQUEST_KEY
import com.example.tfs.presentation.topic.TOPIC_RESULT_KEY
import com.example.tfs.util.CreateEmojiSet
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EmojiDialogFragment : BottomSheetDialogFragment() {

    private lateinit var emojiAdapter: EmojiRecyclerAdapter
    private lateinit var emojiSheetRecycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.bsd_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        emojiSheetRecycler = view.findViewById(R.id.rvEmoji)
        emojiAdapter = EmojiRecyclerAdapter{ emojiCode: Int ->
            setFragmentResult(
                TOPIC_REQUEST_KEY,
                bundleOf(TOPIC_RESULT_KEY to emojiCode)
            )
            dismiss()
        }
        emojiSheetRecycler.adapter = emojiAdapter

        emojiSheetRecycler.layoutManager =
            GridLayoutManager(context, 7, GridLayoutManager.VERTICAL, false)

        emojiAdapter.submitList(CreateEmojiSet.createEmojiSet())
    }
}



