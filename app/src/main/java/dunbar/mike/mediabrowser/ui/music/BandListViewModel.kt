package dunbar.mike.mediabrowser.ui.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dunbar.mike.mediabrowser.data.music.Band
import dunbar.mike.mediabrowser.data.music.MusicRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BandListViewModel @Inject constructor(private val musicRepo: MusicRepo) : ViewModel() {
    private val _uiState = MutableStateFlow<BandListUiState>(BandListUiState.Loading)
    val uiState: StateFlow<BandListUiState> = _uiState

    init {
        viewModelScope.launch {
            musicRepo.getBands()
                .onSuccess { _uiState.value = BandListUiState.Success(it) }
                .onFailure { _uiState.value = BandListUiState.Error(it.message ?: "Unknown error") }
        }
    }
}

sealed interface BandListUiState {
    data object Loading : BandListUiState

    data class Success(val bands: List<Band>) : BandListUiState

    data class Error(val message: String) : BandListUiState

}