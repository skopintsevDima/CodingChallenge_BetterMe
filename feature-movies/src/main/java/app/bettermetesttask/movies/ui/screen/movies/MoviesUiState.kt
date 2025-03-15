package app.bettermetesttask.movies.ui.screen.movies

import app.bettermetesttask.domainmovies.entries.Movie

sealed class MoviesUiState {
    data object Initial : MoviesUiState()
    data object Loading : MoviesUiState()
    data class Data(val movies: List<Movie>) : MoviesUiState()
    data class Error(val errorMessage: String) : MoviesUiState()
}