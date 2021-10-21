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
import com.example.tfs.EMOJI_START_CODE_POINT
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EmojiDialogFragment : BottomSheetDialogFragment() {

    private lateinit var rootView: View
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
        emojiSheetRecycler = rootView.findViewById(R.id.rvEmoji)
        emojiAdapter = EmojiRecyclerAdapter(EmojiClickListener { emojiCode: Int ->
            setFragmentResult(
                REQUEST_KEY,
                bundleOf(RESULT_KEY to emojiCode)
            )
            dismiss()
        })
        emojiSheetRecycler.adapter = emojiAdapter

        val emojiSet = List(150) { EMOJI_START_CODE_POINT + it }

        emojiSheetRecycler.layoutManager =
            GridLayoutManager(context, 7, GridLayoutManager.VERTICAL, false)

        emojiAdapter.submitList(emojiSet)
    }
}
