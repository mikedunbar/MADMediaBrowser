package dunbar.mike.mediabrowser.ui.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dunbar.mike.mediabrowser.data.music.Band
import dunbar.mike.mediabrowser.data.music.MusicRepository
import dunbar.mike.mediabrowser.util.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BandListViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val logger: Logger,

    ) : ViewModel() {
    private val _uiState = MutableStateFlow<BandListUiState>(BandListUiState.Loading)
    val uiState: StateFlow<BandListUiState> = _uiState

    //TODO survive a configuration change
    private var currentPage = 1
    private val bands = mutableListOf<Band>() // TODO thread safety?

    init {
        viewModelScope.launch {
            getBands()
        }
    }

    fun nextPage() {
        currentPage++
        getBands()
    }

    private fun getBands() = viewModelScope.launch {
        logger.d(TAG, "getBands: $currentPage")
        musicRepository.getBands(currentPage)
            .onSuccess {
                bands.addAll(it)
                _uiState.value = BandListUiState.Success(bands = bands, page = currentPage)
            }
            .onFailure { _uiState.value = BandListUiState.Error(it.message ?: "Unknown error") }
    }

    companion object {
        const val TAG = "DEBUG:BandListViewModel"
    }

}

sealed interface BandListUiState {
    data object Loading : BandListUiState

    data class Success(val page: Int,val bands: List<Band>) : BandListUiState

    data class Error(val message: String) : BandListUiState

}