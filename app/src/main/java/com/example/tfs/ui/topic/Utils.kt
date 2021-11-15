package com.example.tfs.ui.topic

fun String.getUnicodeGlyph(): String {
    val builder = StringBuilder()

    builder.apply {
        this@getUnicodeGlyph.split("-").onEach {
            appendCodePoint(Integer.parseInt(it, 16))
        }
    }
    return builder.toString()
}