package com.example.tfs.customviews

class Reaction (val emoji: Int, var count: Int, val userId: List<Int>? = null, var isClicked: Boolean = false)

class Post (val reaction: MutableList<Reaction> = mutableListOf(), val message: String, val isOwner: Boolean = false)