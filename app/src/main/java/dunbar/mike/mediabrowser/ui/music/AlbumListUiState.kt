package dunbar.mike.mediabrowser.ui.music

import dunbar.mike.mediabrowser.data.music.Album

sealed interface AlbumListUiState {
    data object Loading : AlbumListUiState

    data class Success(val bandId: String, val page: Int, val albums: List<Album>) : AlbumListUiState

    data class Error(val message: String) : AlbumListUiState

}