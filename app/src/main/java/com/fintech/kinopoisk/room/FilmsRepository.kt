package com.fintech.kinopoisk.room

import android.content.Context
import androidx.room.Room

class FilmsRepository(context: Context) : FavouriteFilmDao {
    private val db by lazy {
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .addTypeConverter(GenreConverter())
            .addTypeConverter(CountryConverter())
            .build()
    }

    private val favouriteFilmDao by lazy {
        db.getFavouriteFilmDao()
    }

    override fun get(filmId: Int): FavouriteFilm? = favouriteFilmDao.get(filmId)

    override fun add(film: FavouriteFilm) = favouriteFilmDao.add(film)

    override fun deleteById(filmId: Int) = favouriteFilmDao.deleteById(filmId)

    override fun getAll(): List<FavouriteFilm> = favouriteFilmDao.getAll()

    override fun getFilmsIds(): List<Int> = favouriteFilmDao.getFilmsIds()

    companion object {
        private const val DATABASE_NAME = "films_app"
    }
}