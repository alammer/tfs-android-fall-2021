package com.example.tfs.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.tfs.domain.streams.StreamItemList
import org.jetbrains.annotations.NotNull

@Entity(tableName = "streams")
data class LocalStream(

    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "stream_id")
    val streamId: Int,

    @NotNull
    @ColumnInfo(name = "stream_name")
    val streamName: String,

    @NotNull
    @ColumnInfo(name = "is_subscribed")
    val isSubscribed: Boolean = false,

    @field:TypeConverters(Converters::class)
    @ColumnInfo(name = "topics")
    val topics: List<String>,
)

fun LocalStream.toDomainStream(isExpanded: Boolean = false) =
    StreamItemList.StreamItem(id = streamId,
        name = streamName,
        topics = topics,
        expanded = isExpanded)
