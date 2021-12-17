package com.example.tfs.database.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import org.jetbrains.annotations.NotNull


@Entity(tableName = "posts") //TODO UNIQUE KEY for backend replace self-sending posts
data class LocalPost(

    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "post_id")
    val postId: Int = -1,

    @NotNull
    @ColumnInfo(name = "topic_name")
    val topicName: String = "",

    @NotNull
    @ColumnInfo(name = "stream_name")
    val streamName: String = "",

    @NotNull
    @ColumnInfo(name = "sender_id")
    val senderId: Int,

    @NotNull
    @ColumnInfo(name = "is_self")
    val isSelf: Boolean,

    @NotNull
    @ColumnInfo(name = "sender_name")
    val senderName: String = "",

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String? = null,

    @NotNull
    @ColumnInfo(name = "timestamp")
    val timeStamp: Long,

    @field:TypeConverters(Converters::class)
    @ColumnInfo(name = "flags")
    val postFlags: List<String> = emptyList(),
)

@Entity(
    tableName = "reactions",
    primaryKeys = ["owner_post_id", "emoji_code", "user_id"],
    foreignKeys = [
        ForeignKey(entity = LocalPost::class,
            parentColumns = ["post_id"],
            childColumns = ["owner_post_id"],
            onDelete = CASCADE)
    ],
    /*indices = [Index(value = ["owner_post_id"], unique = true)]*/
)
data class LocalReaction(

    @NotNull
    @ColumnInfo(name = "owner_post_id")
    val postId: Int,

    @NotNull
    @ColumnInfo(name = "emoji_name")
    val name: String,

    @NotNull
    @ColumnInfo(name = "emoji_code")
    val code: String,

    @NotNull
    @ColumnInfo(name = "user_id")
    val userId: Int,

    @NotNull
    @ColumnInfo(name = "is_clicked")
    val isClicked: Boolean,

    @NotNull
    @ColumnInfo(name = "is_custom")
    val isCustom: Boolean = false,
)

data class PostWithReaction(
    @Embedded
    val post: LocalPost,
    @Relation(
        parentColumn = "post_id",
        entityColumn = "owner_post_id",
    )
    val reaction: List<LocalReaction>,
)
