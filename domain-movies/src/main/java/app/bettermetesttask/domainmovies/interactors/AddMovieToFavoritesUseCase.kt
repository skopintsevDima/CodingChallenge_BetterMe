package app.bettermetesttask.domainmovies.interactors

import app.bettermetesttask.domaincore.interactor.UseCaseIoDispatcherWithRequest
import app.bettermetesttask.domainmovies.repository.MoviesRepository
import javax.inject.Inject

class AddMovieToFavoritesUseCase @Inject constructor(
    private val repository: MoviesRepository
): UseCaseIoDispatcherWithRequest<Unit, Int>() {
    override suspend fun invoke() {
        repository.addMovieToFavorites(request)
    }
}