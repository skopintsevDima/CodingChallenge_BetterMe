package app.bettermetesttask.movies.screen.movies.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.featurecommon.injection.utils.Injectable
import app.bettermetesttask.featurecommon.injection.viewmodel.SimpleViewModelProviderFactory
import app.bettermetesttask.movies.screen.movies.MoviesState
import app.bettermetesttask.movies.screen.movies.MoviesViewModel
import coil3.compose.AsyncImage
import javax.inject.Inject
import javax.inject.Provider

class MoviesComposeFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelProvider: Provider<MoviesViewModel>

    private val viewModel by viewModels<MoviesViewModel> {
        SimpleViewModelProviderFactory(
            viewModelProvider
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                val viewState by viewModel.moviesStateFlow.collectAsState()
                MoviesComposeScreen(
                    viewState,
                    onViewLoaded = {
                        viewModel.loadMovies()
                    },
                    onLikeMovie = { movie ->
                        viewModel.likeMovie(movie)
                    },
                    onMovieClicked = { movieId ->
                        val action = MoviesComposeFragmentDirections.actionMoviesToDetails(movieId)
                        findNavController().navigate(action)
                    }
                )
            }
        }
    }
}

@Composable
private fun MoviesComposeScreen(
    moviesState: MoviesState,
    onViewLoaded: () -> Unit,
    onLikeMovie: (Movie) -> Unit,
    onMovieClicked: (Int) -> Unit
) {
    LaunchedEffect(Unit) {
        onViewLoaded()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when (moviesState) {
            MoviesState.Initial -> {}
            is MoviesState.Loaded -> {
                LazyColumn {
                    items(moviesState.movies) { item ->
                        MovieItem(
                            movie = item,
                            onLikeClicked = { onLikeMovie(item) },
                            onMovieClicked = onMovieClicked
                        )
                    }
                }
            }

            MoviesState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun MovieItem(
    movie: Movie,
    onLikeClicked: (Int) -> Unit,
    onMovieClicked: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onMovieClicked(movie.id) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = movie.posterPath,
                contentDescription = "Movie Poster",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = movie.title, fontSize = 18.sp, color = Color.Black)
                Text(text = movie.description, fontSize = 14.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(onClick = { onLikeClicked(movie.id) }) {
                Icon(
                    imageVector = if (movie.liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like Button",
                    tint = if (movie.liked) Color.Red else Color.Gray
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
private fun PreviewsMoviesComposeScreen() {
    MoviesComposeScreen(
        MoviesState.Loaded(
            List(20) { index ->
                Movie(
                    index,
                    "Title $index",
                    "Overview $index",
                    null,
                    liked = index % 2 == 0,
                )
            }
        ),
        onLikeMovie = {},
        onViewLoaded = {},
        onMovieClicked = {},
    )
}