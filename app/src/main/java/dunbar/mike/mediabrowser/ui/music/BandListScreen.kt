package dunbar.mike.mediabrowser.ui.music

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dunbar.mike.mediabrowser.R
import dunbar.mike.mediabrowser.data.Band
import dunbar.mike.mediabrowser.ui.theme.MediaBrowserTheme
import dunbar.mike.mediabrowser.util.Logger

@Composable
fun BandListScreenRoot(
    logger: Logger,
    viewModel: BandListViewModel = hiltViewModel<BandListViewModel>(),
    onClickBand: (String) -> Unit = {}
) {
    logger.d("BandListScreen", "Loading Band List Screen")
    BandListScreen(state = viewModel.bandList.collectAsState().value, onClickBand)
}

@Composable
fun BandListScreen(
    state: List<Band>,
    onClickBand: (String) -> Unit = {},
) {
    BandCardList(
        bandList = state,
        onClickBand = onClickBand,
    )
}

@Preview
@Composable
fun BandListScreenPreview() {
    MediaBrowserTheme {
        Surface {
            BandListScreen(
                state = listOf(
                    Band("Widespread Panic", "Rock", "Widespread Panic"),
                    Band("Metallica", "Heavy Metal", "Metallica"),
                    Band("Outkast", "Hip Hop", "Outkast")
                )
            )
        }
    }
}

@Preview
@Composable
fun BandListScreenPreviewDark() {
    MediaBrowserTheme(darkTheme = true) {
        Surface {
            BandListScreen(
                state = listOf(
                    Band("Widespread Panic", "Rock", "Widespread Panic"),
                    Band("Metallica", "Heavy Metal", "Metallica"),
                    Band("Outkast", "Hip Hop", "Outkast")
                )
            )
        }
    }
}

@Composable
fun BandCardList(
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

@Preview
@Composable
fun BandCardListPreview() {
    MediaBrowserTheme(darkTheme = false) {
        Surface {
            BandCardList(
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

@Preview
@Composable
fun BandCardListPreviewDark() {
    MediaBrowserTheme(darkTheme = true) {
        Surface {
            BandCardList(
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

@Preview
@Composable
fun BandCardPreview() {
    MediaBrowserTheme {
        Surface {
            BandCard(Band("Outkast", "Hip Hop", "Outkast")) {}
        }
    }
}

@Preview
@Composable
fun BandCardPreviewDark() {
    MediaBrowserTheme(darkTheme = true) {
        Surface {
            BandCard(Band("Outkast", "Hip Hop", "Outkast")) {}
        }
    }
}