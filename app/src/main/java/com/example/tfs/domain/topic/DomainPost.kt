package com.example.tfs.domain.topic

import android.text.SpannableString
import com.example.tfs.common.baseadapter.AdapterItem


data class DomainPostDate(val postDate: String) : AdapterItem

data class DomainUserPost(
    val id: Int,
    val userId: Int,
    val userName: String,
    val reaction: List<UiItemReaction> = emptyList(),
    val message: String,
    val avatar: String? = null,
    val timeStamp: Long,
    val content: CharSequence,
) : AdapterItem

data class DomainOwnerPost(
    val id: Int,
    val reaction: List<UiItemReaction> = emptyList(),
    val message: String,
    val timeStamp: Long,
) : AdapterItem


