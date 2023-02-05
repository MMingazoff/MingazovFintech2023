package com.fintech.kinopoisk.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.fintech.kinopoisk.R
import com.fintech.kinopoisk.databinding.FragmentFavouriteBinding
import com.fintech.kinopoisk.recyclerview.FavouriteFilmsAdapter
import com.fintech.kinopoisk.recyclerview.SpaceItemDecoration
import com.fintech.kinopoisk.room.FilmsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavouriteFragment : Fragment(R.layout.fragment_favourite) {
    private var binding: FragmentFavouriteBinding? = null
    private var adapter: FavouriteFilmsAdapter? = null
    private var repository: FilmsRepository? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentFavouriteBinding.inflate(layoutInflater)
        repository = FilmsRepository(requireContext())
        val itemDecoration = SpaceItemDecoration(requireContext(), 16f)
        binding?.mainRv?.addItemDecoration(itemDecoration)
        lifecycleScope.launch(Dispatchers.IO) {
            val films = repository?.getAll()?.toMutableList()
            adapter = FavouriteFilmsAdapter(
                films!!,
                requireContext(),
                this@FavouriteFragment::onFilmClicked
            )
            withContext(Dispatchers.Main) {
                binding?.mainRv?.adapter = adapter
            }
        }
    }

    private fun onFilmClicked(filmId: Int) {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val navOptions = NavOptions.Builder()
                .setLaunchSingleTop(false)
                .setEnterAnim(android.R.anim.slide_in_left)
                .setExitAnim(android.R.anim.slide_out_right)
                .setPopEnterAnim(android.R.anim.slide_in_left)
                .setPopExitAnim(android.R.anim.slide_out_right)
                .build()
            findNavController().navigate(
                R.id.action_favouriteFragment_to_descriptionFragment,
                Bundle().apply {
                    putInt(DescriptionFragment.FILM_ID, filmId)
                    putBoolean(DescriptionFragment.IS_FAVOURITE, true)
                },
                navOptions
            )
        } else
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(
                    R.id.description_container,
                    DescriptionFragment.getInstance(filmId, true)
                )
                .commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}