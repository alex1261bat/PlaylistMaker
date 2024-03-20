package com.example.playlistmaker.util

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson

@ProvidedTypeConverter
class ListConverter(private val gson: Gson) {

    @TypeConverter
    fun listToString(list: List<String>): String = gson.toJson(list)

    @TypeConverter
    fun stringToList(str: String): List<String> = gson.fromJson(str, Array<String>::class.java).toList()
}
