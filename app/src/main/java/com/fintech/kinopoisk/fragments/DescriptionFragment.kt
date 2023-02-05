package com.fintech.kinopoisk.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.fintech.kinopoisk.R
import com.fintech.kinopoisk.databinding.FragmentDescriptionBinding
import com.fintech.kinopoisk.network.Network
import com.fintech.kinopoisk.network.response.Film
import com.fintech.kinopoisk.room.FavouriteFilm
import com.fintech.kinopoisk.room.FilmsRepository
import com.google.gson.internal.LinkedTreeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class DescriptionFragment : Fragment(R.layout.fragment_description) {
    private var binding: FragmentDescriptionBinding? = null
    private var repository: FilmsRepository? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentDescriptionBinding.inflate(layoutInflater)
        repository = FilmsRepository(requireContext())
        arguments?.run {
            val filmId = getInt(FILM_ID)
            getBoolean(IS_FAVOURITE).let {
                if (it)
                    getFilmFromDb(filmId)
                else
                    getFilmRequest(filmId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding?.root
    }

    private fun loadFilm(film: Film) {
        binding?.run {
            toolbar.title = film.name
            tvDescription.text = film.description
            tvCountries.text = resources.getString(
                R.string.countries_label,
                film.countries.joinToString { it.country }
            )
            tvGenres.text = resources.getString(
                R.string.genres_label,
                film.genres.joinToString { it.genre }
            )
            tvYear.text = resources.getString(
                R.string.year_label,
                film.year ?: "-"
            )
            Glide.with(this@DescriptionFragment)
                .load(film.poster)
                .into(posterIv)
        }
    }

    private fun loadFilmFromDb(film: FavouriteFilm) {
        binding?.run {
            toolbar.title = film.name
            tvDescription.text = film.description
            tvCountries.text = resources.getString(
                R.string.countries_label,
                (film.countries as List<LinkedTreeMap<String, String>>).joinToString {
                    it.values.first()
                }
            )
            tvGenres.text = resources.getString(
                R.string.genres_label,
                (film.genres as List<LinkedTreeMap<String, String>>).joinToString {
                    it.values.first()
                }
            )
            tvYear.text = resources.getString(
                R.string.year_label,
                film.year ?: "-"
            )
            val file = File(
                context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "${film.id}.png"
            )
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                posterIv.setImageBitmap(bitmap)
            }
        }
    }

    private fun getFilmRequest(filmId: Int) {
        Network.kinopoiskService.getFilmById(filmId).enqueue(object : Callback<Film> {
            override fun onResponse(
                call: Call<Film>,
                response: Response<Film>
            ) {
                if (!response.isSuccessful)
                    return
                loadFilm(response.body()!!)
            }

            override fun onFailure(call: Call<Film>, t: Throwable) {
                Log.i("error", t.message.toString())
            }

        })
    }

    private fun getFilmFromDb(filmId: Int) {
        lifecycleScope.launch(Dispatchers.Main) {
            val film = async(Dispatchers.IO) { repository?.get(filmId) }
            loadFilmFromDb(film.await()!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        fun getInstance(filmId: Int, isFavourite: Boolean) = DescriptionFragment().apply {
            arguments = Bundle().apply {
                putInt(FILM_ID, filmId)
                putBoolean(IS_FAVOURITE, isFavourite)
            }
        }

        const val FILM_ID = "FILM_ID"
        const val IS_FAVOURITE = "IS_FAVOURITE"
    }
}
