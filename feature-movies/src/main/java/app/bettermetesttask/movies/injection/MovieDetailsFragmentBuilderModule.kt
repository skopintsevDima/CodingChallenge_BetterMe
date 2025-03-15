package app.bettermetesttask.movies.injection

import app.bettermetesttask.featurecommon.injection.scopes.FragmentScope
import app.bettermetesttask.movies.ui.screen.details.MovieDetailsComposeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MovieDetailsFragmentBuilderModule {
    @FragmentScope
    @ContributesAndroidInjector
    abstract fun createMovieDetailsComposeFragmentInjector(): MovieDetailsComposeFragment
}