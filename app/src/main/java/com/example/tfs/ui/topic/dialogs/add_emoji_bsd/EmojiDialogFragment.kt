package com.example.tfs.ui.topic.dialogs.add_emoji_bsd

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.ui.topic.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EmojiDialogFragment : BottomSheetDialogFragment() {

    private lateinit var emojiAdapter: EmojiRecyclerAdapter
    private lateinit var emojiSheetRecycler: RecyclerView

    private val emojiMap by lazy {
        createEmojiMap()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.emoji_bsd_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        emojiSheetRecycler = view.findViewById(R.id.rvEmoji)
        emojiSheetRecycler.setHasFixedSize(true)
        emojiAdapter = EmojiRecyclerAdapter { emojiCode: String ->
            val emojiName = emojiMap.keys.firstOrNull { emojiMap[it]?.second == emojiCode }
            val emojiApiCode = emojiMap[emojiName]?.first
            val response = bundleOf(
                EMOJI_RESPONSE_POST to requireArguments().getInt(POST_KEY, -1),
                EMOJI_RESPONSE_NAME to emojiName,
                EMOJI_RESPONSE_CODE to emojiApiCode
            )
            setFragmentResult(
                EMOJI_REQUEST_KEY,
                bundleOf(EMOJI_RESPONSE_KEY to response),
            )
            dismiss()
        }
        emojiSheetRecycler.adapter = emojiAdapter

        emojiSheetRecycler.layoutManager =
            GridLayoutManager(context, 7, GridLayoutManager.VERTICAL, false)

        emojiAdapter.submitList(emojiMap.values.map { (_, unicodeCodePoint) -> unicodeCodePoint }
            .toSet().toList())
    }

    private fun createEmojiMap(): Map<String, Pair<String, String>> {

        val emojiMap = mutableMapOf<String, Pair<String, String>>()

        requireActivity().assets
            .open("zulip_emoji_map")
            .reader()
            .useLines { file ->
                file.forEach { line ->
                    val (emojiName, emojiId) = line.split(":")
                    emojiMap[emojiName] = emojiId to emojiId.getUnicodeGlyph()
                }
            }
        return emojiMap.toMap()
    }

    companion object {

        private const val POST_KEY = "post_id"

        fun newInstance(
            postId: Int,
        ): EmojiDialogFragment {
            val fragment = EmojiDialogFragment()
            val arguments = Bundle()
            arguments.putInt(POST_KEY, postId)
            fragment.arguments = arguments
            return fragment
        }
    }

}



