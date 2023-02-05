package com.fintech.kinopoisk.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.fintech.kinopoisk.R
import com.fintech.kinopoisk.databinding.FragmentHomeBinding
import com.fintech.kinopoisk.network.Network
import com.fintech.kinopoisk.network.response.FilmFromList
import com.fintech.kinopoisk.network.response.FilmsPageResponse
import com.fintech.kinopoisk.recyclerview.FilmsAdapter
import com.fintech.kinopoisk.recyclerview.PaginationScrollListener
import com.fintech.kinopoisk.recyclerview.SpaceItemDecoration
import com.fintech.kinopoisk.room.FilmsRepository
import com.fintech.kinopoisk.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment(R.layout.fragment_home) {
    private var binding: FragmentHomeBinding? = null
    private var adapter: FilmsAdapter? = null
    private var repository: FilmsRepository? = null
    private var isLastPage = false
    private var isLoading = false
    private var pageNumber: Int = 1
    private var favouriteFilmIds: List<Int> = emptyList()

    init {
        getFilmsPage()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentHomeBinding.inflate(layoutInflater)
        repository = FilmsRepository(requireContext())
        binding?.run {
            Glide.with(this@HomeFragment)
                .load(R.drawable.loading)
                .into(ivLoading)
            val itemDecoration = SpaceItemDecoration(requireContext(), 16f)
            mainRv.addItemDecoration(itemDecoration)
            mainRv.addOnScrollListener(
                object : PaginationScrollListener(mainRv.layoutManager as LinearLayoutManager) {
                    override fun isLastPage(): Boolean = isLastPage

                    override fun isLoading(): Boolean = isLoading

                    override fun loadMoreItems() {
                        isLoading = true
                        getFilmsPage()
                    }
                }
            )
        }
    }

    private fun getFilmsPage() {
        Network.kinopoiskService.getFilms(pageNumber).enqueue(object : Callback<FilmsPageResponse> {
            override fun onResponse(
                call: Call<FilmsPageResponse>,
                response: Response<FilmsPageResponse>
            ) {
                binding?.groupConnectionError?.visibility = View.GONE
                if (!response.isSuccessful)
                    return
                val body = response.body()!!
                binding?.ivLoading?.visibility = View.GONE
                if (adapter == null)
                    lifecycleScope.launch(Dispatchers.IO) {
                        favouriteFilmIds = repository?.getFilmsIds() ?: emptyList()
                        withContext(Dispatchers.Main) {
                            setAdapter(body.films.onEach {
                                if (it.id in favouriteFilmIds)
                                    it.isFavourite = true
                            })
                        }
                    }
                else {
                    adapter?.addFilms(body.films.onEach {
                        if (it.id in favouriteFilmIds)
                            it.isFavourite = true
                    })
                    isLoading = false
                }
                if (pageNumber >= Constants.MAX_PAGES)
                    isLastPage = true
                else
                    pageNumber += 1
            }

            override fun onFailure(call: Call<FilmsPageResponse>, t: Throwable) {
                binding?.ivLoading?.visibility = View.GONE
                if (adapter == null) {
                    binding?.run {
                        btnReload.setOnClickListener {
                            getFilmsPage()
                        }
                        groupConnectionError.visibility = View.VISIBLE
                    }
                    return
                }
                Toast.makeText(
                    context,
                    requireContext().getString(R.string.loading_error),
                    Toast.LENGTH_SHORT
                ).show()
                isLoading = false
            }

        })
    }

    private fun setAdapter(films: MutableList<FilmFromList>) {
        activity?.let {
            adapter = FilmsAdapter(
                films,
                Glide.with(this@HomeFragment),
                repository,
                requireContext(),
                lifecycleScope,
                this@HomeFragment::onFilmClicked
            )
            binding?.mainRv?.adapter = adapter
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
                R.id.action_homeFragment_to_descriptionFragment,
                Bundle().apply {
                    putInt(DescriptionFragment.FILM_ID, filmId)
                    putBoolean(DescriptionFragment.IS_FAVOURITE, false)
                },
                navOptions
            )
        } else
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(
                    R.id.description_container,
                    DescriptionFragment.getInstance(filmId, false)
                )
                .commit()
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch(Dispatchers.IO) {
            val newFavouriteFilmIds = repository?.getFilmsIds() ?: emptyList()
            val difference = favouriteFilmIds.subtract(newFavouriteFilmIds.toSet())
            withContext(Dispatchers.Main) {
                if (difference.isNotEmpty()) {
                    adapter?.updateFilmsRemovedFromFavourite(difference)
                    adapter?.notifyDataSetChanged()
                    favouriteFilmIds = newFavouriteFilmIds
                }
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

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}