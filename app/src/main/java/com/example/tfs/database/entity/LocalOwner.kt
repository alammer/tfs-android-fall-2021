package com.example.tfs.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "owner")
data class LocalOwner(

    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "user_id")
    val id: Int,

    @ColumnInfo(name = "user_name")
    val userName: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "role")
    val role: Int,

    @ColumnInfo(name = "date_joined")
    val dateJoined: String,

    @ColumnInfo(name = "user_status")
    val isActive: Boolean,

    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String?,

    @ColumnInfo(name = "is_admin")
    val isAdmin: Boolean = false,

    @ColumnInfo(name = "is_owner")
    val isOwner: Boolean = false,

    @ColumnInfo(name = "is_guest")
    val isGuest: Boolean = false,

    @ColumnInfo(name = "is_bot")
    val isBot: Boolean = false,
)