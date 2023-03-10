package com.fintech.kinopoisk.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fintech.kinopoisk.databinding.ItemFilmBinding
import com.fintech.kinopoisk.room.FavouriteFilm
import com.fintech.kinopoisk.room.FilmsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FavouriteFilmsAdapter(
    private val list: MutableList<FavouriteFilm>,
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val repository: FilmsRepository?,
    private val action: (Int) -> Unit
) : RecyclerView.Adapter<FavouriteFilmHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FavouriteFilmHolder(
        ItemFilmBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        context,
        action
    ) {
        val toDeleteIndex = list.indexOf(it)
        list.removeAt(toDeleteIndex)
        notifyItemRemoved(toDeleteIndex)
        coroutineScope.launch(Dispatchers.IO) {
            repository?.deleteById(it.id)
        }
    }

    override fun onBindViewHolder(holder: FavouriteFilmHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size
}