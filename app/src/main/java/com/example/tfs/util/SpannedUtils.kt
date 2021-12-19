package com.example.tfs.util

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned

fun SpannableString.applySpan(
    what: Any,
    start: Int = 0,
    end: Int = length
): SpannableString = apply {
    setSpan(what, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
}

