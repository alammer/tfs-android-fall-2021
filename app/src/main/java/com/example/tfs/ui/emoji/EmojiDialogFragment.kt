package com.example.tfs.ui.emoji

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.REQUEST_KEY
import com.example.tfs.RESULT_KEY
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EmojiDialogFragment : BottomSheetDialogFragment() {

    private lateinit var emojiAdapter: EmojiRecyclerAdapter
    private lateinit var emojiSheetRecycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.bottom_sheet_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        emojiSheetRecycler = view.findViewById(R.id.rvEmoji)
        emojiAdapter = EmojiRecyclerAdapter(EmojiClickListener { emojiCode: Int ->
            setFragmentResult(
                REQUEST_KEY,
                bundleOf(RESULT_KEY to emojiCode)
            )
            dismiss()
        })
        emojiSheetRecycler.adapter = emojiAdapter

        val emojiSet = mutableListOf<Int>()

        emojiSet.addAll((EMOJI_FACE_START_CODE..EMOJI_FACE_END_CODE))
        emojiSet.addAll((EMOJI_GESTURE_START_CODE..EMOJI_GESTURE_END_CODE))
        emojiSet.addAll((EMOJI_VAR_START_CODE..EMOJI_VAR_END_CODE))

        emojiSheetRecycler.layoutManager =
            GridLayoutManager(context, 7, GridLayoutManager.VERTICAL, false)

        emojiAdapter.submitList(emojiSet)
    }
}

const val EMOJI_FACE_START_CODE = 0x1f600
const val EMOJI_FACE_END_CODE = 0x1f644
const val EMOJI_GESTURE_START_CODE = 0x1f645
const val EMOJI_GESTURE_END_CODE = 0x1f64f
const val EMOJI_VAR_START_CODE = 0x1f90c
const val EMOJI_VAR_END_CODE = 0x1f92f


