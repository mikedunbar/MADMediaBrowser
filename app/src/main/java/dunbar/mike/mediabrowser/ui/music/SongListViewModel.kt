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
class SongListViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val logger: Logger,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val logTag = "SongListViewModel"
    private val albumId: String = checkNotNull(savedStateHandle["albumId"])

    private val _uiState = MutableStateFlow(SongListUiState(isLoading = true))
    val uiState: StateFlow<SongListUiState> = _uiState.asStateFlow()

    private var fetchSongsJob: Job? = null

    init {
        fetchSongs()
    }

    fun handleIntent(intent: SongListIntent) {
        when(intent) {
            SongListIntent.ClearError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun fetchSongs() {
        fetchSongsJob?.cancel()
        fetchSongsJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, songs = emptyList()) }
            musicRepository.getSongs(albumId)
                .onSuccess { songs ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = null, songs = songs) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "Unknown error") }

                }
        }
    }
}