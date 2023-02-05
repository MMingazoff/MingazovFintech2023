package com.fintech.kinopoisk.room

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.fintech.kinopoisk.network.response.Country
import com.fintech.kinopoisk.network.response.Genre
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
abstract class Converter<T> {
    @TypeConverter
    fun listToString(value: List<T>): String {
        val gson = Gson()
        val type = object : TypeToken<List<T>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun stringToList(value: String): List<T> {
        val gson = Gson()
        val type = object : TypeToken<List<T>>() {}.type
        return gson.fromJson(value, type)
    }
}

@ProvidedTypeConverter
class GenreConverter : Converter<Genre>()

@ProvidedTypeConverter
class CountryConverter : Converter<Country>()