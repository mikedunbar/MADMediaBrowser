package dunbar.mike.mediabrowser.ui.music

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dunbar.mike.mediabrowser.data.music.Album
import dunbar.mike.mediabrowser.data.music.MusicRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumListViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val bandId: String = checkNotNull(savedStateHandle["bandId"])

    private val _uiState = MutableStateFlow<AlbumListUiState>(AlbumListUiState.Loading)
    val uiState: StateFlow<AlbumListUiState> = _uiState.asStateFlow()

    private val albums = mutableListOf<Album>()
    private var albumsJob: Job? = null

    fun onLoadMore() {
        getAlbums()
    }

    init {
        getAlbums()
    }


    private fun getAlbums() {
        albumsJob?.cancel()
        albumsJob = viewModelScope.launch {
            val page = (_uiState.value as? AlbumListUiState.Success)?.let { it.page + 1 } ?: 1
            musicRepository.getAlbums(bandId)
                .onSuccess {
                    albums.addAll(it)
                    _uiState.update { AlbumListUiState.Success(bandId, page, albums) }
                }
                .onFailure { error ->
                    _uiState.update { AlbumListUiState.Error(error.message ?: "Unknown error") }
                }
        }

    }
}

