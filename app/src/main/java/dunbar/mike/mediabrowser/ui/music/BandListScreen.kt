package dunbar.mike.mediabrowser.ui.music

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
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
import dunbar.mike.mediabrowser.ui.theme.MediaBrowserTheme
import dunbar.mike.mediabrowser.util.Logger

@Composable
fun BandListScreenRoot(
    logger: Logger,
    viewModel: BandListViewModel = hiltViewModel<BandListViewModel>(),
    onClickBand: (String) -> Unit = {}
) {
    logger.d("BandListScreen", "Loading Band List Screen")
    BandListScreen(uiState = viewModel.uiState.collectAsStateWithLifecycle().value, onClickBand)
}

@Composable
fun BandListScreen(
    uiState: BandListUiState,
    onClickBand: (String) -> Unit = {},
) {
    when (uiState) {
        is BandListUiState.Success -> {
            BandListView(
                bandList = uiState.bands,
                onClickBand = onClickBand,
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
fun LoadingView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Loading...")
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
fun ErrorView(message: String) {
    Column()
    {
        Text(text = "Unable to load bands list", fontSize = MaterialTheme.typography.titleLarge.fontSize)
        Text(text = message, fontStyle = FontStyle.Italic)
    }

}

@Composable
fun BandListView(
    bandList: List<Band>,
    onClickBand: (String) -> Unit,
) {
    val scrollState = rememberLazyListState()

    LazyColumn(state = scrollState) {
        items(bandList.size) {
            BandCard(bandList[it], onClickBand)
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
            Text(band.genre)
        }
    }
}

//region Preview

class BandListUiStateProvider : PreviewParameterProvider<BandListUiState> {
    override val values = sequenceOf(
        BandListUiState.Loading,
        BandListUiState.Error("You appear to be offline"),
        BandListUiState.Success(
            listOf(
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
fun BandListScreenDataPreview(@PreviewParameter(BandListUiStateProvider::class) uiState: BandListUiState) {
    MediaBrowserTheme {
        Surface {
            BandListScreen(uiState = uiState)
        }
    }
}

@PreviewLightDark
@Composable
private fun LoadingPreview() {
    MediaBrowserTheme {
        Surface {
            LoadingView()
        }
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun ErrorPreview() {
    MediaBrowserTheme {
        Surface {
            ErrorView("You appear to be offline")
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
                    Band("Widespread Panic", "Rock", "Widespread Panic"),
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