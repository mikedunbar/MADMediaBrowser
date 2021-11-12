package dunbar.mike.musicbrowser.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dunbar.mike.musicbrowser.R
import dunbar.mike.musicbrowser.model.Band
import dunbar.mike.musicbrowser.ui.theme.MusicBrowserTheme
import dunbar.mike.musicbrowser.util.Logger

@Composable
fun BandsScreenBody(
    logger: Logger,
    viewModel: BandListViewModel,
    onClickBand: (String) -> Unit = {},
) {
    logger.d("BandsScreenBody", "Loading Bands Screen")
    BandCardList(
        bandList = viewModel.bandList.value!!,
        onClickBand = onClickBand,
    )
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
    MusicBrowserTheme {
        BandCardList(
            bandList = listOf(
                Band("Widespread Panic", "Rock"),
                Band("Metallica", "Heavy Metal"),
                Band("Outkast", "Hip Hop")
            ),
            onClickBand = {},
        )
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
                onClick = {onClickBand(band.name)}
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
    MusicBrowserTheme(darkTheme = false) {
        Surface {
            BandCard(Band("Outkast", "Hip Hop")) {}
        }

    }
}

@Preview
@Composable
fun BandCardPreviewDark() {
    MusicBrowserTheme(darkTheme = true) {
        Surface {
            BandCard(Band("Outkast", "Hip Hop")) {}
        }

    }
}