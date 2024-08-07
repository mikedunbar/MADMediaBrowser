@file:OptIn(ExperimentalMaterial3Api::class)

package dunbar.mike.mediabrowser.ui.music

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import dunbar.mike.mediabrowser.R
import dunbar.mike.mediabrowser.data.music.Band
import dunbar.mike.mediabrowser.ui.shared.ErrorView
import dunbar.mike.mediabrowser.ui.shared.LoadingView
import dunbar.mike.mediabrowser.ui.theme.MediaBrowserTheme
import kotlinx.coroutines.Dispatchers

@Composable
fun BandListScreenRoot(
    viewModel: BandListViewModel = hiltViewModel<BandListViewModel>(),
    onClickBand: (String) -> Unit,
) {
    BandListScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        searchString = viewModel.searchQuery.value,
        onClickBand = onClickBand,
        onLoadMore = viewModel::nextPage,
        onSearchChanged = viewModel::search
    )
}

@Composable
fun BandListScreen(
    uiState: BandListUiState,
    searchString: String = "",
    onClickBand: (String) -> Unit = {},
    onLoadMore: () -> Unit = {},
    onSearchChanged: (String) -> Unit = {},
) {
    when (uiState) {
        is BandListUiState.Initial -> {
            BandSearchCard(searchString = searchString, onSearchChanged = onSearchChanged)
        }

        is BandListUiState.Success -> {
            Column {
                BandSearchCard(searchString = searchString, onSearchChanged = onSearchChanged)
                BandListView(
                    bandList = uiState.bands,
                    onClickBand = onClickBand,
                    onLoadMore = onLoadMore
                )
            }

        }

        is BandListUiState.Error -> {
            ErrorView(uiState.message)
        }

        is BandListUiState.Loading -> {
            BandSearchCard(searchString = searchString, onSearchChanged = onSearchChanged)
            LoadingView()
        }
    }
}

@Composable
fun BandSearchCard(
    searchString: String,
    onSearchChanged: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Search for Bands")
        TextField(value = searchString, onValueChange = onSearchChanged)
    }
}

@Composable
fun BandListView(
    bandList: List<Band>,
    onClickBand: (String) -> Unit,
    onLoadMore: () -> Unit,
) {
    val listState = rememberLazyListState()

    val reachedBottom by remember {
        derivedStateOf {
            val bufferSize = 5
            val minItemsForPaging = 15
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val lastVisibleItemIndex = lastVisibleItem?.index ?: 0
            val hasReachedBottom = lastVisibleItemIndex >= totalItemsCount - bufferSize - 1
            hasReachedBottom && totalItemsCount > minItemsForPaging
        }
    }

    LaunchedEffect(reachedBottom) {
        if (reachedBottom) {
            onLoadMore()
        }
    }

    LazyColumn(state = listState) {
        items(
            items = bandList,
            key = { it.id }
        ) {
            BandCard(it, onClickBand)
        }
    }
}

@Composable
fun BandCard(
    band: Band,
    onClickBand: (String) -> Unit,
) {
    val context = LocalContext.current
    val placeholder = R.drawable.ic_baseline_music_note_24
    val imageUrl = "https://archive.org/services/img/${band.id}"

    // Build an ImageRequest with Coil
    val imageRequest = ImageRequest.Builder(context)
        .data(imageUrl)
        .dispatcher(Dispatchers.IO)
        .memoryCacheKey(imageUrl)
        .diskCacheKey(imageUrl)
        .placeholder(placeholder)
        .error(placeholder)
        .fallback(placeholder)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .selectable(
                selected = false,
                onClick = { onClickBand(band.id) }
            )
    )
    {
        // Load and display the image with AsyncImage
        AsyncImage(
            model = imageRequest,
            contentDescription = "Image Description",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape),
            contentScale = ContentScale.Crop,
        )
        Column(modifier = Modifier.padding(5.dp))
        {
            Text(band.name, fontWeight = FontWeight.Bold)
            Text(band.description)
        }
    }
}

//region Preview

class BandListUiStateProvider : PreviewParameterProvider<BandListUiState> {
    override val values = sequenceOf(
        BandListUiState.Initial,
        BandListUiState.Loading,
        BandListUiState.Error("You appear to be offline"),
        BandListUiState.Success(
            page = 1,
            bands = listOf(
                Band("Widespread Panic", "Rock", "Widespread Panic"),
                Band("Metallica", "Heavy Metal", "Metallica"),
                Band("Outkast", "Hip Hop", "Outkast"),
                Band("Nirvana", "Grunge", "Nirvana"),
                Band("Widespread Panic", "Rock", "Widespread Panic2"),
                Band("Metallica", "Heavy Metal", "Metallica2"),
                Band("Outkast", "Hip Hop", "Outkast2"),
                Band("Nirvana", "Grunge", "Nirvana2"),
            )
        )
    )

}

@PreviewLightDark
@PreviewScreenSizes
@Composable
fun BandListScreenPreview(@PreviewParameter(BandListUiStateProvider::class) uiState: BandListUiState) {
    MediaBrowserTheme {
        Surface {
            BandListScreen(uiState = uiState, searchString = "Gratef")
        }
    }
}

@PreviewLightDark
@Composable
fun BandListPreview() {
    MediaBrowserTheme {
        Surface {
            BandListView(
                bandList = listOf(
                    Band("Widespread Panic", "Rock", "WidespreadPanic"),
                    Band("Metallica", "Heavy Metal", "Metallica"),
                    Band("Outkast", "Hip Hop", "Outkast")
                ),
                onClickBand = {},
                onLoadMore = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
fun BandCardPreview() {
    MediaBrowserTheme {
        Surface {
            BandCard(Band("Outkast", "Hip Hop", "Outkast")) {}
        }
    }
}

//endregion