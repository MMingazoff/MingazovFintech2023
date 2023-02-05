package com.fintech.kinopoisk.network.response

import com.squareup.moshi.Json

data class Film(
    @Json(name = "nameRu")
    val name: String = "",
    @Json(name = "posterUrl")
    val poster: String = "",
    val description: String = "",
    val countries: List<Country> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val year: Int? = 0
)