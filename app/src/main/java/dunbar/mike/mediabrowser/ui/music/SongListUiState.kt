package dunbar.mike.mediabrowser.ui.music

import dunbar.mike.mediabrowser.data.music.Song

data class SongListUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val songs: List<Song> = emptyList(),
)
