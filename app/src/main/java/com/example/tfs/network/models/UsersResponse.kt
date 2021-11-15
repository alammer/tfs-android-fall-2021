package com.example.tfs.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsersResponse(
    @SerialName("members")
    val userList: List<User> = emptyList()
)

@Serializable
data class User(
    @SerialName("user_id")
    val id: Int,
    @SerialName("full_name")
    val name: String,
    @SerialName("email")
    val email: String,
    @SerialName("role")
    val role: Int,
    @SerialName("date_joined")
    val dateJoined: String,
    @SerialName("is_active")
    val isActive: Boolean,
    @SerialName("avatar_version")
    val avatarVersion: Int,
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    @SerialName("is_admin")
    val isAdmin: Boolean = false,
    @SerialName("is_owner")
    val isOwner: Boolean = false,
    @SerialName("is_guest")
    val isGuest: Boolean = false,
    @SerialName("is_bot")
    val isBot: Boolean = false,
)