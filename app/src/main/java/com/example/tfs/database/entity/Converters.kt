package com.example.tfs.database.entity

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun toListOfStrings(string: String): List<String> {
        return string.split("|")
    }

    @TypeConverter
    fun fromListOfStrings(stringList: List<String>): String {
        return stringList.joinToString("|")
    }

}