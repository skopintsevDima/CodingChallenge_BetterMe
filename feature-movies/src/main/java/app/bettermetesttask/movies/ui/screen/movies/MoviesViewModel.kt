package app.bettermetesttask.movies.ui.screen.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.domainmovies.interactors.AddMovieToFavoritesUseCase
import app.bettermetesttask.domainmovies.interactors.ObserveMoviesUseCase
import app.bettermetesttask.domainmovies.interactors.RemoveMovieFromFavoritesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MoviesViewModel @Inject constructor(
    private val observeMoviesUseCase: ObserveMoviesUseCase,
    private val likeMovieUseCase: AddMovieToFavoritesUseCase,
    private val dislikeMovieUseCase: RemoveMovieFromFavoritesUseCase,
    private val adapter: MoviesAdapter
) : ViewModel() {
    private val _uiState: MutableStateFlow<MoviesUiState> = MutableStateFlow(MoviesUiState.Initial)
    val uiState: StateFlow<MoviesUiState>
        get() = _uiState.asStateFlow()

    fun loadMovies() {
        if (_uiState.value is MoviesUiState.Data) return

        viewModelScope.launch {
            _uiState.emit(MoviesUiState.Loading)
            invokeCatching("Loading movies failed") {
                observeMoviesUseCase.invoke()
                    .collect { result ->
                        if (result is Result.Success) {
                            _uiState.emit(MoviesUiState.Data(result.data))
                            adapter.submitList(result.data)
                        }
                    }
            }
        }
    }

    fun likeMovie(movie: Movie) {
        viewModelScope.launch {
            invokeCatching("Like/dislike failed") {
                if (!movie.liked) {
                    likeMovieUseCase.request(movie.id)
                } else {
                    dislikeMovieUseCase.request(movie.id)
                }
            }
        }
    }

    fun openMovieDetails(movie: Movie) {
        // Implemented in Compose Fragment only
    }

    private suspend fun invokeCatching(prefix: String, invoke: suspend () -> Unit) {
        runCatching { invoke() }.onFailure { error ->
            val errorMessage = "$prefix: ${error.message.toString()}"
            _uiState.emit(MoviesUiState.Error(errorMessage))
        }
    }
}