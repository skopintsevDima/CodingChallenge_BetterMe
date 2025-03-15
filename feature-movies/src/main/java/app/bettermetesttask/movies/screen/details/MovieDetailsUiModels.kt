package app.bettermetesttask.movies.screen.details

import app.bettermetesttask.domainmovies.entries.Movie

sealed class MovieDetailsUiState {
    data object Initial : MovieDetailsUiState()
    data object Loading : MovieDetailsUiState()
    data class Data(val movie: Movie) : MovieDetailsUiState()

    val asData: Data?
        get() = this as? Data
}