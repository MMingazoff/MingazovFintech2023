package com.fintech.kinopoisk.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [FavouriteFilm::class], version = 1)
@TypeConverters(GenreConverter::class, CountryConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getFavouriteFilmDao(): FavouriteFilmDao
}