package dunbar.mike.mediabrowser.ui.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dunbar.mike.mediabrowser.data.music.Band
import dunbar.mike.mediabrowser.data.music.MusicRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BandListViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<BandListUiState>(BandListUiState.Loading)
    val uiState: StateFlow<BandListUiState> = _uiState
        .onSubscription { getBands() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BandListUiState.Loading)

    private val bands = mutableListOf<Band>()
    private var bandsJob: Job? = null

    fun nextPage() {
        getBands()
    }

    private fun getBands() {
        bandsJob?.cancel()
        bandsJob = viewModelScope.launch {
            val page = (_uiState.value as? BandListUiState.Success)?.let { it.page + 1 } ?: 1
            musicRepository.getBands(page)
                .onSuccess {
                    bands.addAll(it)
                    _uiState.update { BandListUiState.Success(bands = bands, page = page) }
                }
                .onFailure { error ->
                    _uiState.update { BandListUiState.Error(error.message ?: "Unknown error") }
                }
        }
    }
}

sealed interface BandListUiState {
    data object Loading : BandListUiState

    data class Success(val page: Int, val bands: List<Band>) : BandListUiState

    data class Error(val message: String) : BandListUiState

}