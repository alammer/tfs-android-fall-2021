package com.example.tfs.domain.contacts

data class DomainUser(
    val id: Int,
    val name: String,
    val email: String,
    val isActive: Boolean,
    val avatarUrl: String? = null,
    val presenceData: UserPresence,
)

data class UserPresence(
    val status: String,
    val timestamp: Long
)