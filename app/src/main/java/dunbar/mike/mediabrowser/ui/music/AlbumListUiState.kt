package dunbar.mike.mediabrowser.ui.music

import dunbar.mike.mediabrowser.data.music.Album

data class AlbumListUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val page: Int = 0,
    val albums: List<Album> = emptyList(),
)