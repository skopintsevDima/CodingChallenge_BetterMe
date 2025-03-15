package app.bettermetesttask.domainmovies.interactors

import app.bettermetesttask.domaincore.utils.coroutines.AppDispatchers
import app.bettermetesttask.domaincore.utils.coroutines.DispatcherProvider
import app.bettermetesttask.domainmovies.repository.MoviesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class RemoveMovieFromFavoritesUseCaseTest {
    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher: TestDispatcher = StandardTestDispatcher(testScheduler)

    private val repository: MoviesRepository = mock()

    private lateinit var useCase: RemoveMovieFromFavoritesUseCase

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
        useCase = RemoveMovieFromFavoritesUseCase(repository)
    }

    @Test
    fun `invoke calls repository to add movie to favorites`() = runTest {
        useCase.request(1)

        verify(repository).removeMovieFromFavorites(1)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
