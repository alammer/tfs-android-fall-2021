package com.example.tfs.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.tfs.domain.streams.DomainStream
import org.jetbrains.annotations.NotNull

@Entity(tableName = "remoteStreams")
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

    @NotNull
    @ColumnInfo(name = "is_expanded")
    val isExpanded: Boolean = false,

    @field:TypeConverters(Converters::class)
    @ColumnInfo(name = "topics")
    val topics: List<String>,
)



