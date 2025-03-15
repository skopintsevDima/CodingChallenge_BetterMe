package app.bettermetesttask.movies.screen.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domaincore.utils.coroutines.AppDispatchers
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

    private val moviesMutableFlow: MutableStateFlow<MoviesState> = MutableStateFlow(MoviesState.Initial)

    val moviesStateFlow: StateFlow<MoviesState>
        get() = moviesMutableFlow.asStateFlow()

    fun loadMovies() {
        viewModelScope.launch(AppDispatchers.io()) {
            observeMoviesUseCase()
                .collect { result ->
                    if (result is Result.Success) {
                        moviesMutableFlow.emit(MoviesState.Loaded(result.data))
                        adapter.submitList(result.data)
                    }
                }
        }
    }

    fun likeMovie(movie: Movie) {
        viewModelScope.launch(AppDispatchers.io()) {
            if (!movie.liked) {
                likeMovieUseCase(movie.id)
            } else {
                dislikeMovieUseCase(movie.id)
            }
        }
    }

    fun openMovieDetails(movie: Movie) {
        // Implemented in Compose Fragment only
    }
}