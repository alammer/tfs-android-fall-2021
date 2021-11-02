package com.example.tfs.domain.contacts

data class Contact(
    val userId: Int,
    val userName: String,
    val userEmail: String,
    val userState: Int,
    val userImage: Int?
)