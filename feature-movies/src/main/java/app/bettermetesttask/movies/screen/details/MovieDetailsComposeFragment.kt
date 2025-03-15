package app.bettermetesttask.movies.screen.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.navigation.fragment.navArgs
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.featurecommon.injection.utils.Injectable
import app.bettermetesttask.featurecommon.injection.viewmodel.SimpleViewModelProviderFactory
import coil3.compose.AsyncImage
import javax.inject.Inject
import javax.inject.Provider

class MovieDetailsComposeFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelProvider: Provider<MovieDetailsViewModel>

    private val args: MovieDetailsComposeFragmentArgs by navArgs()

    private val viewModel by viewModels<MovieDetailsViewModel> {
        SimpleViewModelProviderFactory(
            viewModelProvider
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.init(args.movieId)

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                val uiState by viewModel.uiState.collectAsState()
                MovieDetailsComposeScreen(
                    uiState = uiState,
                    onViewLoaded = { viewModel.loadMovie() },
                    onLikeClicked = { viewModel.likeMovie() }
                )
            }
        }
    }
}

@Composable
private fun MovieDetailsComposeScreen(
    uiState: MovieDetailsUiState,
    onViewLoaded: () -> Unit,
    onLikeClicked: () -> Unit
) {
    LaunchedEffect(Unit) {
        onViewLoaded()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when (uiState) {
            MovieDetailsUiState.Initial -> {}

            MovieDetailsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is MovieDetailsUiState.Data -> {
                MovieDetails(
                    movie = uiState.movie,
                    onLikeClicked = onLikeClicked
                )
            }
        }
    }
}

@Composable
private fun MovieDetails(
    movie: Movie,
    onLikeClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        AsyncImage(
            model = movie.posterPath,
            contentDescription = "Movie Poster",
            modifier = Modifier
                .size(300.dp, 500.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Gray)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = movie.title, fontSize = 22.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = movie.description, fontSize = 18.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = onLikeClicked) {
                Icon(
                    modifier = Modifier.size(36.dp),
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
    MovieDetailsComposeScreen(
        uiState = MovieDetailsUiState.Data(
            Movie(
                0,
                "Title $0",
                "Overview $0",
                null,
                liked = true,
            )
        ),
        onViewLoaded = {},
        onLikeClicked = {}
    )
}