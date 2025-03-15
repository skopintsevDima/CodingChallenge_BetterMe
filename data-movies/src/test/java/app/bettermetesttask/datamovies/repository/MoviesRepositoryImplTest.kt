package app.bettermetesttask.datamovies.repository

import app.bettermetesttask.datamovies.database.entities.MovieEntity
import app.bettermetesttask.datamovies.repository.stores.MoviesLocalStore
import app.bettermetesttask.datamovies.repository.stores.MoviesMapper
import app.bettermetesttask.datamovies.repository.stores.MoviesRestStore
import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domaincore.utils.connectivity.ConnectivityManager
import app.bettermetesttask.domainmovies.entries.Movie
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class MoviesRepositoryImplTest {

    private val connectivityManager: ConnectivityManager = mock()
    private val localStore: MoviesLocalStore = mock()
    private val restStore: MoviesRestStore = mock()
    private val mapper: MoviesMapper = mock()

    private lateinit var repository: MoviesRepositoryImpl

    @BeforeEach
    fun setup() {
        repository = MoviesRepositoryImpl(
            connectivityManager = connectivityManager,
            localStore = localStore,
            restStore = restStore,
            mapper = mapper
        )
    }

    @Test
    fun `getMovies returns local movies when refresh is successful`() = runTest {
        val localMovies = listOf(MovieEntity(1, "Title", "Desc", "posterPath"))
        val mappedMovies = listOf(Movie(1, "Title", "Desc", "posterPath", liked = false))

        `when`(localStore.getMovies()).thenReturn(localMovies)
        `when`(mapper.mapFromLocal(any())).thenReturn(mappedMovies.first())
        `when`(connectivityManager.isNetworkAvailable()).thenReturn(true)
        `when`(restStore.getMovies()).thenReturn(emptyList())

        val result = repository.getMovies()

        assert(result is Result.Success)
        assertEquals(mappedMovies, (result as Result.Success).data)

        verify(localStore).getMovies()
    }

    @Test
    fun `getMovies returns local movies when network is unavailable`() = runTest {
        val localMovies = listOf(MovieEntity(1, "Title", "Desc", "posterPath"))
        val mappedMovies = listOf(Movie(1, "Title", "Desc", "posterPath", liked = false))

        `when`(localStore.getMovies()).thenReturn(localMovies)
        `when`(mapper.mapFromLocal(any())).thenReturn(mappedMovies.first())
        `when`(connectivityManager.isNetworkAvailable()).thenReturn(false)

        val result = repository.getMovies()

        assert(result is Result.Success)
        assertEquals(mappedMovies, (result as Result.Success).data)

        verify(localStore).getMovies()
        verify(restStore, never()).getMovies()
    }

    @Test
    fun `getMovies returns local movies even if API call fails`() = runTest {
        val localMovies = listOf(MovieEntity(1, "Title", "Desc", "posterPath"))
        val mappedMovies = listOf(Movie(1, "Title", "Desc", "posterPath", liked = false))

        `when`(localStore.getMovies()).thenReturn(localMovies)
        `when`(mapper.mapFromLocal(any())).thenReturn(mappedMovies.first())
        `when`(connectivityManager.isNetworkAvailable()).thenReturn(true)
        `when`(restStore.getMovies()).thenThrow(RuntimeException("API failure"))

        val result = repository.getMovies()

        assert(result is Result.Success)
        assertEquals(mappedMovies, (result as Result.Success).data)

        verify(localStore).getMovies()
        verify(restStore).getMovies()
    }

    @Test
    fun `getMovie returns mapped movie from local store`() = runTest {
        val movieEntity = MovieEntity(1, "Title", "Desc", "posterPath")
        val mappedMovie = Movie(1, "Title", "Desc", "posterPath", liked = false)

        `when`(localStore.getMovie(1)).thenReturn(movieEntity)
        `when`(mapper.mapFromLocal(movieEntity)).thenReturn(mappedMovie)

        val result = repository.getMovie(1)

        assert(result is Result.Success)
        assertEquals(mappedMovie, (result as Result.Success).data)

        verify(localStore).getMovie(1)
    }

    @Test
    fun `observeLikedMovieIds returns Flow of liked movie IDs`() = runTest {
        val likedMoviesFlow = flowOf(listOf(1, 2, 3))

        `when`(localStore.observeLikedMoviesIds()).thenReturn(likedMoviesFlow)

        val result = repository.observeLikedMovieIds().first()

        assertEquals(listOf(1, 2, 3), result)
    }

    @Test
    fun `observeLikedMovie returns Flow of liked movie ID`() = runTest {
        val likedMovieFlow = flowOf(1)

        `when`(localStore.observeLikedMovie(1)).thenReturn(likedMovieFlow)

        val result = repository.observeLikedMovie(1).first()

        assertEquals(1, result)
    }

    @Test
    fun `addMovieToFavorites calls localStore likeMovie`() = runTest {
        repository.addMovieToFavorites(1)

        verify(localStore).likeMovie(1)
    }

    @Test
    fun `removeMovieFromFavorites calls localStore dislikeMovie`() = runTest {
        repository.removeMovieFromFavorites(1)

        verify(localStore).dislikeMovie(1)
    }
}