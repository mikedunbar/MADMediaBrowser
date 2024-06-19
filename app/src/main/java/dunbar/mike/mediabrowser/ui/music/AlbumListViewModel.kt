package dunbar.mike.mediabrowser.ui.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dunbar.mike.mediabrowser.data.music.Album
import dunbar.mike.mediabrowser.data.music.MusicRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumListViewModel @Inject constructor(private val musicRepository: MusicRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<AlbumListUiState>(AlbumListUiState.Loading)
    val uiState: StateFlow<AlbumListUiState> = _uiState

    private var bandId = ""
    private val albums = mutableListOf<Album>()
    private var albumsJob: Job? = null

    fun setBandId(bandId: String) {
        this.bandId = bandId
        albums.clear()
        _uiState.update { AlbumListUiState.Loading }
        getAlbums()
    }

    fun onLoadMore() {
        getAlbums()
    }

    private fun getAlbums() {
        if (bandId.isEmpty()) {
            throw IllegalStateException("Band ID cannot be empty")
        }
        albumsJob?.cancel()
        albumsJob = viewModelScope.launch {
            val page = (_uiState.value as? AlbumListUiState.Success)?.let { it.page + 1 } ?: 1
            musicRepository.getAlbums(bandId)
                .onSuccess {
                    albums.addAll(it)
                    _uiState.update { AlbumListUiState.Success(bandId, page, albums) }
                }
                .onFailure { error ->
                    _uiState.update {  AlbumListUiState.Error(error.message ?: "Unknown error") }
                }
        }
    }
}

sealed interface AlbumListUiState {
    data object Loading : AlbumListUiState

    data class Success(val bandId: String, val page: Int, val albums: List<Album>) : AlbumListUiState

    data class Error(val message: String) : AlbumListUiState

}