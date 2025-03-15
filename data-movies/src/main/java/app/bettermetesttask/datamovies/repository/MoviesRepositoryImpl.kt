package app.bettermetesttask.datamovies.repository

import app.bettermetesttask.datamovies.repository.stores.MoviesLocalStore
import app.bettermetesttask.datamovies.repository.stores.MoviesMapper
import app.bettermetesttask.datamovies.repository.stores.MoviesRestStore
import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.domainmovies.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(
    private val localStore: MoviesLocalStore,
    private val restStore: MoviesRestStore,
    private val mapper: MoviesMapper
) : MoviesRepository {
    override suspend fun getMovies(): Result<List<Movie>> {
        val refreshLocalMoviesAttempt = tryRefreshLocalMovies()
        val localMovies = localStore.getMovies().map { mapper.mapFromLocal(it) }

        return when (refreshLocalMoviesAttempt) {
            is Result.Success -> {
                Result.of { localMovies }
            }
            is Result.Error -> {
                Timber.d("Refresh local movies failed: ${refreshLocalMoviesAttempt.error}")
                Result.of { localMovies }
            }
        }
    }

    private fun tryRefreshLocalMovies(): Result<Unit> {
        return runBlocking {
            runCatching { restStore.getMovies() }.fold(
                onSuccess = { apiMovies ->
                    val dbMovies = apiMovies.map { mapper.mapToLocal(it) }
                    Result.of { localStore.updateMovies(dbMovies) }
                },
                onFailure = { Result.Error(it) }
            )
        }
    }

    override suspend fun getMovie(id: Int): Result<Movie> {
        return Result.of { mapper.mapFromLocal(localStore.getMovie(id)) }
    }

    override fun observeLikedMovieIds(): Flow<List<Int>> {
        return localStore.observeLikedMoviesIds()
    }

    override fun observeLikedMovie(movieId: Int): Flow<Int?> {
        return localStore.observeLikedMovie(movieId)
    }

    override suspend fun addMovieToFavorites(movieId: Int) {
        localStore.likeMovie(movieId)
    }

    override suspend fun removeMovieFromFavorites(movieId: Int) {
        localStore.dislikeMovie(movieId)
    }
}