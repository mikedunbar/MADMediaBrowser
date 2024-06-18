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
import dunbar.mike.mediabrowser.data.music.Album
import dunbar.mike.mediabrowser.data.music.createTestAlbum
import dunbar.mike.mediabrowser.ui.shared.ErrorView
import dunbar.mike.mediabrowser.ui.shared.LoadingView
import dunbar.mike.mediabrowser.ui.theme.MediaBrowserTheme
import dunbar.mike.mediabrowser.util.formatDateLocalMedium

@Composable
fun AlbumListScreenRoot(
    viewModel: AlbumListViewModel = hiltViewModel<AlbumListViewModel>(),
    onClickAlbum: (String) -> Unit
) {
    AlbumListScreen(uiState = viewModel.uiState.collectAsStateWithLifecycle().value, onClickAlbum = onClickAlbum, onLoadMore = viewModel::onLoadMore )
}

@Composable
fun AlbumListScreen(
    uiState: AlbumListUiState,
    onClickAlbum: (String) -> Unit,
    onLoadMore: () -> Unit,
) {
    when (uiState) {
        is AlbumListUiState.Success -> {
            AlbumListView(albumList = uiState.albums, onClickAlbum = onClickAlbum, onLoadMore = onLoadMore)
        }

        is AlbumListUiState.Error -> {
            ErrorView(uiState.message)
        }

        is AlbumListUiState.Loading -> {
            LoadingView()
        }
    }
}

@Composable
fun AlbumListView(
    albumList: List<Album>,
    onClickAlbum: (String) -> Unit,
    onLoadMore: () -> Unit,
) {
    val listState = rememberLazyListState()

    val reachedBottom by remember {
        derivedStateOf {
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val lastVisibleItemIndex = lastVisibleItem?.index ?: 0
            val hasReachedBottom = lastVisibleItemIndex != 0 && lastVisibleItemIndex == totalItemsCount - 1
            hasReachedBottom
        }
    }

    LaunchedEffect(reachedBottom) {
        if (reachedBottom) {
            onLoadMore()
        }
    }

    LazyColumn(state = listState) {
        items(
            items = albumList,
            key = { it.id }
        ) {
            AlbumCard(album = it, onClickAlbum = onClickAlbum)
        }
    }
}

@Composable
fun AlbumCard(
    album: Album,
    onClickAlbum: (String) -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .selectable(
                selected = true,
                onClick = { onClickAlbum(album.name) }
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
            Text(album.name, fontWeight = FontWeight.Bold)
            Text(formatDateLocalMedium(album.releaseDate))
            Text("${album.songs.size} Songs")
        }
    }
}

//region Preview

class UiStateProvider : PreviewParameterProvider<AlbumListUiState> {
    override val values = sequenceOf(
        AlbumListUiState.Error("Unable to fetch artists for album"),
        AlbumListUiState.Loading,
        AlbumListUiState.Success(
            bandId = "DatBand",
            page = 1,
            albums = listOf(
                createTestAlbum(name = "Bombs and Butterflies"),
                createTestAlbum(name = "Magical Mystery Tour"),
                createTestAlbum(name = "Running Wide"),
                createTestAlbum(name = "Under Da Blood"),
                createTestAlbum(name = "Breaking Bad Soundtrack"),
                createTestAlbum(name = "Exile on Main St."),
            )
        )
    )
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
fun AlbumListScreenPreview(@PreviewParameter(UiStateProvider::class) uiState: AlbumListUiState) {
    MediaBrowserTheme {
        Surface {
            AlbumListScreen(uiState = uiState, onClickAlbum = {}, onLoadMore = {})
        }
    }
}

//endregion