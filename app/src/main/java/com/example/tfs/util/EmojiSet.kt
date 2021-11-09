package com.example.tfs.util

object CreateEmojiSet {

    fun createEmojiSet(): List<Int> {
        val emojiSet = mutableListOf<Int>()

        emojiSet.addAll((EMOJI_FACE_START_CODE..EMOJI_FACE_END_CODE))
        emojiSet.addAll((EMOJI_GESTURE_START_CODE..EMOJI_GESTURE_END_CODE))
        emojiSet.addAll((EMOJI_VAR_START_CODE..EMOJI_VAR_END_CODE))

        return emojiSet.toList()
    }
}

const val EMOJI_FACE_START_CODE = 0x1f600
const val EMOJI_FACE_END_CODE = 0x1f644
const val EMOJI_GESTURE_START_CODE = 0x1f645
const val EMOJI_GESTURE_END_CODE = 0x1f64f
const val EMOJI_VAR_START_CODE = 0x1f90c
const val EMOJI_VAR_END_CODE = 0x1f92f








