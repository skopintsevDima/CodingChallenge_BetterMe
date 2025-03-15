package app.bettermetesttask.movies.injection

import app.bettermetesttask.featurecommon.injection.scopes.FragmentScope
import app.bettermetesttask.movies.screen.movies.MoviesFragment
import app.bettermetesttask.movies.screen.movies.compose.MoviesComposeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MoviesFragmentBuildersModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = [MoviesScreenModule::class])
    abstract fun createMoviesFragmentInjector(): MoviesFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [MoviesScreenModule::class])
    abstract fun createMoviesComposeFragmentInjector(): MoviesComposeFragment
}