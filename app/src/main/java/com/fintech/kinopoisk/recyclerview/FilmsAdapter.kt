package com.fintech.kinopoisk.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.fintech.kinopoisk.databinding.ItemFilmBinding
import com.fintech.kinopoisk.network.response.FilmFromList
import com.fintech.kinopoisk.room.FilmsRepository
import com.fintech.kinopoisk.util.Constants
import kotlinx.coroutines.CoroutineScope

class FilmsAdapter(
    private val list: MutableList<FilmFromList>,
    private val glide: RequestManager,
    private val repository: FilmsRepository?,
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val action: (Int) -> Unit
) : RecyclerView.Adapter<FilmHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FilmHolder(
        ItemFilmBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        glide,
        repository,
        context,
        coroutineScope,
        action
    )

    override fun onBindViewHolder(holder: FilmHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun addFilms(filmsToAppend: List<FilmFromList>) {
        list.addAll(filmsToAppend)
        notifyItemRangeInserted(list.size - Constants.PAGE_SIZE, Constants.PAGE_SIZE)
    }
}