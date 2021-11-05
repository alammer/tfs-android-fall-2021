package com.example.tfs.domain.contacts

data class Contact(
    val id: Int,
    val name: String,
    val email: String,
    val state: Int,
    val status: String?,
    val avatar: Int?
)