package com.example.tfs.util

import java.text.SimpleDateFormat
import java.util.*

val Long.shortDate
    get() = SimpleDateFormat("d MMM", Locale("ru", "RU"))
        .format(this * 1000L).replace(".", "")

val Long.fullDate
    get() = SimpleDateFormat("d MMMM',' ' 'yyyy", Locale("ru", "RU"))
        .format(this * 1000L).replace(".", "")

val Long.postDate
    get() = SimpleDateFormat("HH:mm d MMMM',' ' 'yyyy", Locale("ru", "RU"))
        .format(this * 1000L).replace(".", "")

val Long.year
    get() = SimpleDateFormat("yyyy", Locale("ru", "RU"))
        .format(this * 1000L).toInt()

fun Long.startOfDay(localOffset: Long) = (this + localOffset) - (this + localOffset) % 86400L