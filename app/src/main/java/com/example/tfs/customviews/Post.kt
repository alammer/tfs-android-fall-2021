package com.example.tfs.customviews

class Reaction (val emoji: Int?, val count: Int?)

class Post (val reaction: List<Reaction>, val message: String)