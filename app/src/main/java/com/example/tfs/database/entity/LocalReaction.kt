package com.example.tfs.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "reactions")
class LocalReaction(

    @NotNull
    @ColumnInfo(name="post_id")
    val postId: Int,

    @NotNull
    @ColumnInfo(name="emoji_name")
    val emojiName: String,

    @NotNull
    @ColumnInfo(name = "emoji_code")
    val emojiCode: String,

    @NotNull
    @ColumnInfo(name="user_id")
    val userId: Int,
)