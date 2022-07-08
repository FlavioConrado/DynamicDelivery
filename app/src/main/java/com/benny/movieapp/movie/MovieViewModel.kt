package com.benny.movieapp.movie


import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.drsync.core.domain.usecase.MovieUseCase

class MovieViewModel (movieUseCase: MovieUseCase) : ViewModel()  {
    val movies = movieUseCase.getAllMovie().asLiveData()
}