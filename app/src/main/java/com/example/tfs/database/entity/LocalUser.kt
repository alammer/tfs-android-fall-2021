package com.example.tfs.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tfs.common.baseadapter.AdapterItem
import org.jetbrains.annotations.NotNull

@Entity(tableName = "contacts")
data class LocalUser(

    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "user_id")
    val id: Int,

    @NotNull
    @ColumnInfo(name = "user_name")
    val userName: String,

    @NotNull
    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String?,

    @NotNull
    @ColumnInfo(name = "user_status")
    val isActive: Boolean,

    @NotNull
    @ColumnInfo(name = "user_state")
    val userState: String = "",

    @NotNull
    @ColumnInfo(name = "last_visit")
    val lastVisit: Long = 0,
) : AdapterItem