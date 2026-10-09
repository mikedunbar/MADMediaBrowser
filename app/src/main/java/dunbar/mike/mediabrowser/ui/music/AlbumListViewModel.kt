package dunbar.mike.mediabrowser.ui.music

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dunbar.mike.mediabrowser.data.music.MusicRepository
import dunbar.mike.mediabrowser.util.Logger
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
    private val logger: Logger,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val logTag = "AlbumListViewModel"
    private val bandId: String = checkNotNull(savedStateHandle["bandId"])

    private val _uiState = MutableStateFlow(AlbumListUiState(isLoading = true))
    val uiState: StateFlow<AlbumListUiState> = _uiState.asStateFlow()

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
            val startPage = _uiState.value.page + 1
            val albums = _uiState.value.albums
            logger.d(logTag, "getAlbums: page=$startPage, albumsSize=${albums.size}")
            _uiState.update { it.copy(isLoading = true) }
            musicRepository.getAlbums(bandId, startPage)
                .onSuccess {
                    _uiState.update { state ->
                        AlbumListUiState(isLoading = false, errorMessage = null, page = startPage, albums = (state.albums + it).toSet().toList())
                    }
                    logger.d(logTag, "getAlbums: success page=${uiState.value.page} albumsSize=${uiState.value.albums.size}")
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "Unknown error") }
                }
        }
    }
}

