package app.bettermetesttask.movies.ui.screen.details

import app.bettermetesttask.domainmovies.entries.Movie

sealed class MovieDetailsUiState {
    data object Initial : MovieDetailsUiState()
    data object Loading : MovieDetailsUiState()
    data class Data(val movie: Movie) : MovieDetailsUiState()
    data class Error(val errorMessage: String) : MovieDetailsUiState()

    val asData: Data?
        get() = this as? Data
}