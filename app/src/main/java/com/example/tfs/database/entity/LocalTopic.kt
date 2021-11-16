package com.example.tfs.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.jetbrains.annotations.NotNull

@Entity(tableName = "topics") //нужен сложный ключ либо внешний либо autogenerated
data class LocalTopic(

    @PrimaryKey
    @NotNull
    @ColumnInfo(name="id")
    val id: Int,

    @NotNull
    @ColumnInfo(name = "topic_name")
    val topicName: String,

    @NotNull
    @ColumnInfo(name = "parent_stream")
    val streamName: String,
)