package com.fintech.kinopoisk.network.response

import com.squareup.moshi.Json

data class FilmFromList(
    @Json(name = "filmId")
    val id: Int = 0,
    @Json(name = "nameRu")
    val name: String = "",
    @Json(name = "posterUrl")
    val poster: String = "",
    val year: String? = "",
    var isFavourite: Boolean = false
)
