package com.fintech.kinopoisk.room

import androidx.room.*

@Dao
interface FavouriteFilmDao {
    @Query("SELECT * FROM favourite_films WHERE id = :filmId")
    fun get(filmId: Int): FavouriteFilm?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(film: FavouriteFilm)

    @Query("DELETE FROM favourite_films WHERE id = :filmId")
    fun deleteById(filmId: Int)

    @Query("SELECT * FROM favourite_films")
    fun getAll(): List<FavouriteFilm>

    @Query("SELECT id FROM favourite_films")
    fun getFilmsIds(): List<Int>
}