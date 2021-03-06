package com.example.tfs.network.models

import com.example.tfs.database.entity.LocalUser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserListResponse(
    @SerialName("members")
    val userList: List<User> = emptyList(),
)

@Serializable
data class UserResponse(
    @SerialName("user")
    val user: User,
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

@Serializable
data class UserPresence(
    @SerialName("presence")
    val userPresence: Presence,
)

@Serializable
data class Presence(
    @SerialName("aggregated")
    val state: AggregatedStatus,
)

@Serializable
data class AggregatedStatus(
    @SerialName("status")
    val userStatus: String,
    @SerialName("timestamp")
    val userTimestamp: Long,
)

fun User.toLocalUser(presence: AggregatedStatus) = LocalUser(
    id = id,
    userName = name,
    email = email,
    avatarUrl = avatarUrl,
    isActive = isActive,
    userState = presence.userStatus,
    lastVisit = presence.userTimestamp
)

