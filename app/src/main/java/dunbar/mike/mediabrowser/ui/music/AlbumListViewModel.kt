package dunbar.mike.mediabrowser.ui.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dunbar.mike.mediabrowser.data.music.Album
import dunbar.mike.mediabrowser.data.music.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumListViewModel @Inject constructor(private val musicRepository: MusicRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<AlbumListUiState>(AlbumListUiState.Loading)
    val uiState: StateFlow<AlbumListUiState> = _uiState

    fun setBand(bandName: String) = viewModelScope.launch {
        musicRepository.getAlbums(bandName)
            .onSuccess { _uiState.value = AlbumListUiState.Success(it) }
            .onFailure { _uiState.value = AlbumListUiState.Error(it.message ?: "Unknown error") }
    }

}

sealed interface AlbumListUiState {
    data object Loading : AlbumListUiState

    data class Success(val albums: List<Album>) : AlbumListUiState

    data class Error(val message: String) : AlbumListUiState

}