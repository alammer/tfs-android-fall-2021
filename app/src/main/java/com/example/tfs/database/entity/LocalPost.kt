package com.example.tfs.database.entity

import androidx.room.*
import org.jetbrains.annotations.NotNull

@Entity(tableName = "posts")
data class LocalPost(

    @PrimaryKey
    @NotNull
    @ColumnInfo(name="post_id")
    val postId: Int,

    @NotNull
    @ColumnInfo(name = "topic_name")
    val topicName: String,

    @NotNull
    @ColumnInfo(name = "stream_name")
    val streamName: String,

    @NotNull
    @ColumnInfo(name = "sender_id")
    val senderId: Int,

    @NotNull
    @ColumnInfo(name = "sender_name")
    val senderName: String,

    @NotNull
    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String?,

    @NotNull
    @ColumnInfo(name = "timestamp")
    val timeStamp: Long,

    @field:TypeConverters(Converters::class)
    @ColumnInfo(name = "flags")
    val postFlags: List<String>,
)

@Entity(tableName = "reactions")
class LocalReaction(

    @PrimaryKey(autoGenerate = true)
    @NotNull
    @ColumnInfo(name="id")
    val id: Int = 0,

    @NotNull
    @ColumnInfo(name="owner_post_id")
    val ownerPostId: Int,

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

@Entity(primaryKeys = ["post_id", "owner_post_id"])
data class PostReactionXRef(
    val post_id: Long,
    val owner_post_id: Long
)

data class PostWithReaction(
    @Embedded val post: LocalPost,
    @Relation(
        parentColumn = "post_id",
        entityColumn = "owner_post_id",
        associateBy = Junction(PostReactionXRef::class)
    )
    val reaction: List<LocalReaction>
)
