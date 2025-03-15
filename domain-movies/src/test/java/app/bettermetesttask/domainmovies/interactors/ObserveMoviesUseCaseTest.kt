package app.bettermetesttask.domainmovies.interactors

import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domaincore.utils.coroutines.AppDispatchers
import app.bettermetesttask.domaincore.utils.coroutines.DispatcherProvider
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.domainmovies.repository.MoviesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock

@ExtendWith(MockitoExtension::class)
class ObserveMoviesUseCaseTest {
    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher: TestDispatcher = StandardTestDispatcher(testScheduler)

    private val repository: MoviesRepository = mock()

    private lateinit var useCase: ObserveMoviesUseCase

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        AppDispatchers.setInstance(object : DispatcherProvider {
            override fun main(): CoroutineDispatcher = testDispatcher
            override fun io(): CoroutineDispatcher = testDispatcher
            override fun default(): CoroutineDispatcher = testDispatcher
            override fun unconfined(): CoroutineDispatcher = testDispatcher
        })
        useCase = ObserveMoviesUseCase(repository)
    }

    @Test
    fun `invoke returns movies with liked status updated`() = runTest {
        val movies = listOf(Movie(1, "Title", "Desc", "posterPath", liked = false))
        val likedMovieIds = listOf(1)

        `when`(repository.getMovies()).thenReturn(Result.Success(movies))
        `when`(repository.observeLikedMovieIds()).thenReturn(flowOf(likedMovieIds))

        val result = useCase.invoke().first()

        assert(result is Result.Success)
        val updatedMovies = (result as Result.Success).data
        assertEquals(1, updatedMovies.size)
        assertEquals(true, updatedMovies.first().liked)
    }

    @Test
    fun `invoke returns error when repository fails to fetch movies`() = runTest {
        val errorResult = Result.Error(Exception("Failed to fetch movies"))

        `when`(repository.getMovies()).thenReturn(errorResult)

        val result = useCase.invoke().first()

        assert(result is Result.Error)
        assertEquals("Failed to fetch movies", (result as Result.Error).error.message)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
