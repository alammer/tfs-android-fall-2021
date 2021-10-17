package com.example.tfs.customviews

class Reaction (val emoji: Int?, val count: Int?, val userId: List<Int>? = null)

class Post (val reaction: List<Reaction>? = null, val message: String, val isOwner: Boolean = false)