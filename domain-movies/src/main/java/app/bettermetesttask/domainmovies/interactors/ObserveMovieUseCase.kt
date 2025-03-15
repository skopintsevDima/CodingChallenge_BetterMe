package app.bettermetesttask.domainmovies.interactors

import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.domainmovies.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveMovieUseCase @Inject constructor(
    private val repository: MoviesRepository
) {
    suspend operator fun invoke(movieId: Int): Flow<Result<Movie>> {
        return when (val result = repository.getMovie(movieId)) {
            is Result.Success -> {
                repository.observeLikedMovie(movieId)
                    .map { likedMovieId ->
                        val movie = result.data.copy(
                            liked = likedMovieId != null
                        )
                        Result.Success(movie)
                    }
            }
            is Result.Error -> {
                flowOf(result)
            }
        }
    }
}