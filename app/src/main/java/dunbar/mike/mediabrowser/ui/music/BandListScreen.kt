package dunbar.mike.mediabrowser.ui.music

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dunbar.mike.mediabrowser.R
import dunbar.mike.mediabrowser.data.music.Band
import dunbar.mike.mediabrowser.ui.shared.ErrorView
import dunbar.mike.mediabrowser.ui.shared.LoadingView
import dunbar.mike.mediabrowser.ui.theme.MediaBrowserTheme
import dunbar.mike.mediabrowser.util.AndroidLogger

val myLogger = AndroidLogger
val TAG = "DEBUG:BandListScreen"

@Composable
fun BandListScreenRoot(
    viewModel: BandListViewModel = hiltViewModel<BandListViewModel>(),
    onClickBand: (String) -> Unit, // TODO - fold into ViewModel?
) {
    BandListScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onClickBand = onClickBand,
        onLoadMore = viewModel::nextPage
    )
}

@Composable
fun BandListScreen(
    uiState: BandListUiState,
    onClickBand: (String) -> Unit = {},
    onLoadMore: () -> Unit = {}
) {
    myLogger.d(TAG, "rendering with uiState=$uiState")
    when (uiState) {
        is BandListUiState.Success -> {
            BandListView(
                bandList = uiState.bands,
                onClickBand = onClickBand,
                onLoadMore = onLoadMore
            )
        }

        is BandListUiState.Error -> {
            ErrorView(uiState.message)
        }

        is BandListUiState.Loading -> {
            LoadingView()
        }
    }
}

@Composable
fun BandListView(
    bandList: List<Band>,
    onClickBand: (String) -> Unit,
    onLoadMore: () -> Unit = {},
) {
    val listState = rememberLazyListState()

    val reachedBottom by remember {
        derivedStateOf {
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val lastVisibleItemIndex = lastVisibleItem?.index ?: 0
            val hasReachedBottom = lastVisibleItemIndex != 0 && lastVisibleItemIndex == totalItemsCount - 1
            myLogger.d(
                TAG,
                "deriving reachedBottom. totalItems=%s, lastVisibleIndex=%s, hasReachedBottom=%s",
                totalItemsCount,
                lastVisibleItemIndex,
                hasReachedBottom
            )
            hasReachedBottom
        }
    }

    LaunchedEffect(reachedBottom) {
        if (reachedBottom) {
            myLogger.d(TAG, "reachedBottom, loading more")
            onLoadMore()
        }
    }

    LazyColumn(state = listState) {
        items(
            items=bandList,
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
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .selectable(
                selected = false, //TODO
                onClick = { onClickBand(band.name) }
            )
    )
    {
        Image(
            painter = painterResource(id = R.drawable.ic_baseline_music_note_24),
            contentDescription = R.string.musical_note.toString(),
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape)
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
        BandListUiState.Loading,
        BandListUiState.Error("You appear to be offline"),
        BandListUiState.Success(
            page = 1,
            bands = listOf(
                Band("Widespread Panic", "Rock", "Widespread Panic"),
                Band("Metallica", "Heavy Metal", "Metallica"),
                Band("Outkast", "Hip Hop", "Outkast"),
                Band("Nirvana", "Grunge", "Nirvana"),
                Band("Widespread Panic", "Rock", "Widespread Panic"),
                Band("Metallica", "Heavy Metal", "Metallica"),
                Band("Outkast", "Hip Hop", "Outkast"),
                Band("Nirvana", "Grunge", "Nirvana"),
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
            BandListScreen(uiState = uiState)
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