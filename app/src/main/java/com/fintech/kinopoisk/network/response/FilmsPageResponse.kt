package com.fintech.kinopoisk.network.response

import com.squareup.moshi.Json

data class FilmsPageResponse(
    @Json(name = "pages_count")
    val pagesCount: Int = 0,
    val films: MutableList<FilmFromList> = mutableListOf()
)
