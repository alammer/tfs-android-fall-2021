package com.example.tfs.domain.contacts

data class Contact(
    val id: Int,
    val name: String,
    val email: String,
    val role: Int = 600,
    val isActive: Boolean = true,
    val profileData: UserProfile? = null,
    val avatarUrl: String? = null,
)

data class UserProfile(
    val email: String,
    val status: String? = null
)