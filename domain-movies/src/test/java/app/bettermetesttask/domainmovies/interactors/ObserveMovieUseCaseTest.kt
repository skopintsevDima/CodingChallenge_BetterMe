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
class ObserveMovieUseCaseTest {
    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher: TestDispatcher = StandardTestDispatcher(testScheduler)

    private val repository: MoviesRepository = mock()

    private lateinit var useCase: ObserveMovieUseCase

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
        useCase = ObserveMovieUseCase(repository)
    }

    @Test
    fun `invoke returns movie with correct liked status`() = runTest {
        val movie = Movie(1, "Title", "Desc", "posterPath", liked = false)
        val likedMovieFlow = flowOf(1) // Simulating that movie ID 1 is liked

        `when`(repository.getMovie(1)).thenReturn(Result.Success(movie))
        `when`(repository.observeLikedMovie(1)).thenReturn(likedMovieFlow)

        useCase.request(1)
        val result = useCase.invoke().first()

        assert(result is Result.Success)
        val updatedMovie = (result as Result.Success).data
        assertEquals(true, updatedMovie.liked)
    }

    @Test
    fun `invoke returns error when movie fetch fails`() = runTest {
        val errorResult = Result.Error(Exception("Movie not found"))

        `when`(repository.getMovie(1)).thenReturn(errorResult)

        useCase.request(1)
        val result = useCase.invoke().first()

        assert(result is Result.Error)
        assertEquals("Movie not found", (result as Result.Error).error.message)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
