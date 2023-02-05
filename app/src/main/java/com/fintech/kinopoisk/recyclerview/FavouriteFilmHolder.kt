package com.fintech.kinopoisk.recyclerview

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.recyclerview.widget.RecyclerView
import com.fintech.kinopoisk.databinding.ItemFilmBinding
import com.fintech.kinopoisk.room.FavouriteFilm
import java.io.File


class FavouriteFilmHolder(
    private val binding: ItemFilmBinding,
    private val context: Context,
    private val action: (Int) -> Unit,
    private val onDelete: (FavouriteFilm) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun onBind(film: FavouriteFilm) {
        binding.run {
            titleTv.text = film.name
            authorTv.text = film.year.toString()
            val file =
                File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "${film.id}.png")
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                posterIv.setImageBitmap(bitmap)
            }
            root.setOnClickListener {
                action(film.id)
            }
            root.setOnLongClickListener {
                if (file.exists())
                    file.delete()
                onDelete(film)
                true
            }
        }
    }
}