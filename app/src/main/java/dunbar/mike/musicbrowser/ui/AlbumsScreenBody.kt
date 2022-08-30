package dunbar.mike.musicbrowser.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import dunbar.mike.musicbrowser.R
import dunbar.mike.musicbrowser.model.Album
import dunbar.mike.musicbrowser.model.createTestAlbumInfo
import dunbar.mike.musicbrowser.model.createTestAlbumList
import dunbar.mike.musicbrowser.ui.theme.MusicBrowserTheme

@Composable
fun AlbumScreenBody(
    viewModel: AlbumListViewModel
) {
    val albumListState = viewModel.albumList.collectAsState()
    AlbumCardList(albumList = albumListState.value)
}

@Composable
fun AlbumCard(
    album: Album
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
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
            Text(album.name, fontWeight = FontWeight.Bold)
            Text(album.releaseDate.toString()) //todo date format
            Text("${album.songs.size} Songs") // todo i18n
        }
    }
}

@Preview
@Composable
fun AlbumCardPreview() {
    MusicBrowserTheme(darkTheme = false) {
        Surface {
            AlbumCard(createTestAlbumInfo())
        }

    }
}

@Preview
@Composable
fun AlbumCardPreviewDark() {
    MusicBrowserTheme(darkTheme = true) {
        Surface {
            AlbumCard(createTestAlbumInfo())
        }

    }
}

@Composable
fun AlbumCardList(albumList: List<Album>) {
    val scrollState = rememberLazyListState()

    LazyColumn(state = scrollState) {
        items(albumList.size) {
            AlbumCard(albumList[it])
        }
    }

}

@Preview
@Composable
fun AlbumCardListPreview() {
    MusicBrowserTheme(darkTheme = false) {
        Surface {
            AlbumCardList(
                albumList = createTestAlbumList("Grateful Dead")
            )
        }
    }
}

@Preview
@Composable
fun AlbumCardListPreviewDark() {
    MusicBrowserTheme(darkTheme = true) {
        Surface {
            AlbumCardList(
                albumList = createTestAlbumList("Grateful Dead")
            )
        }
    }
}