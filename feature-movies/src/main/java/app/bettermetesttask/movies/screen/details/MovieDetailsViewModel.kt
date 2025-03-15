package app.bettermetesttask.movies.screen.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domaincore.utils.coroutines.AppDispatchers
import app.bettermetesttask.domainmovies.interactors.AddMovieToFavoritesUseCase
import app.bettermetesttask.domainmovies.interactors.ObserveMovieUseCase
import app.bettermetesttask.domainmovies.interactors.RemoveMovieFromFavoritesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieDetailsViewModel @Inject constructor(
    private val observeMovieUseCase: ObserveMovieUseCase,
    private val likeMovieUseCase: AddMovieToFavoritesUseCase,
    private val dislikeMovieUseCase: RemoveMovieFromFavoritesUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<MovieDetailsUiState> = MutableStateFlow(
        MovieDetailsUiState.Initial
    )
    val uiState: StateFlow<MovieDetailsUiState>
        get() = _uiState.asStateFlow()

    private var movieId: Int? = null

    fun init(movieId: Int) {
        this.movieId = movieId
    }

    fun loadMovie() {
        viewModelScope.launch(AppDispatchers.io()) {
            movieId?.let {
                observeMovieUseCase.invoke(it)
                    .collectLatest { result ->
                        if (result is Result.Success) {
                            val newState = MovieDetailsUiState.Data(result.data)
                            _uiState.emit(newState)
                        }
                    }
            }
        }
    }

    fun likeMovie() {
        viewModelScope.launch(AppDispatchers.io()) {
            val movie = _uiState.value.asData?.movie ?: return@launch
            if (!movie.liked) {
                likeMovieUseCase(movie.id)
            } else {
                dislikeMovieUseCase(movie.id)
            }
        }
    }
}