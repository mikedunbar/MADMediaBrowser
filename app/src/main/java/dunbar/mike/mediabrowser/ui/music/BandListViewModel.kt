package dunbar.mike.mediabrowser.ui.music

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dunbar.mike.mediabrowser.data.music.Band
import dunbar.mike.mediabrowser.data.music.MusicRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BandListViewModel @Inject constructor(private val musicRepository: MusicRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<BandListUiState>(BandListUiState.Initial)
    val uiState: StateFlow<BandListUiState> = _uiState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BandListUiState.Initial)

    private var bands = mutableListOf<Band>()
    private var bandsJob: Job? = null

    var searchQuery: MutableState<String> = mutableStateOf("")
        private set

    fun nextPage() {
        getBands(newQuery = false)
    }

    fun search(searchString: String) {
        searchQuery.value = searchString
        if (searchString.length < 4) {
            return
        }
        _uiState.update { state ->
            if (state is BandListUiState.Initial) {
                BandListUiState.Loading
            } else {
                state
            }
        }
        getBands(newQuery = true)
    }

    private fun getBands(newQuery: Boolean = false) {
        bandsJob?.cancel()
        bandsJob = viewModelScope.launch {
            var page = (_uiState.value as? BandListUiState.Success)?.page ?: 1
            if (newQuery) {
                bands = mutableListOf()
                page = 1
            } else {
                page += 1
            }
            musicRepository.getBands(searchQuery.value, page)
                .onSuccess {
                    bands.addAll(it)
                    _uiState.update { BandListUiState.Success(bands = bands, page = page) }
                }
                .onFailure { error ->
                    _uiState.update { BandListUiState.Error(message = error.message ?: "Unknown error") }
                }
        }
    }
}

sealed interface BandListUiState {
    data object Initial : BandListUiState
    data object Loading : BandListUiState
    data class Success(val page: Int, val bands: List<Band>) : BandListUiState
    data class Error(val message: String) : BandListUiState
}