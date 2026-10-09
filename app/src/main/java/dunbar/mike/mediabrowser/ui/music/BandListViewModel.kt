package dunbar.mike.mediabrowser.ui.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dunbar.mike.mediabrowser.data.music.MusicRepository
import dunbar.mike.mediabrowser.util.Logger
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class BandListViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val logger: Logger,
) : ViewModel() {
    private val logTag = "BandListViewModel"
    private val _uiState = MutableStateFlow(BandListUiState())
    val uiState: StateFlow<BandListUiState> = _uiState.asStateFlow()
    private val searchQuery = MutableStateFlow("")
    private var bandsJob: Job? = null

    init {
        viewModelScope.launch {
            logger.d(logTag, "init: launched searchQueryFlow")
            searchQuery
                .drop(1)
                .debounce(1000.milliseconds)
                .collect { searchString ->
                    logger.d(logTag, "searchQueryFlow: Collected $searchString")
                    if (searchString.length >= 4) {
                        getBands(newQuery = true)
                    } else {
                        bandsJob?.cancel()
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                bands = emptyList(),
                                page = 1,
                                errorMessage = null
                            )
                        }
                    }
                }
        }
    }

    fun nextPage() {
        if (_uiState.value.searchString.length >= 4) {
            getBands(newQuery = false)
        }
    }

    fun search(searchString: String) {
        logger.d(logTag, "search: $searchString")
        _uiState.update { it.copy(searchString = searchString) }
        searchQuery.value = searchString
    }

    private fun getBands(newQuery: Boolean = false) {
        bandsJob?.cancel()
        bandsJob = viewModelScope.launch {
            val searchString = _uiState.value.searchString
            if (searchString.length < 4) {
                return@launch
            }
            if (newQuery) {
                _uiState.update { old ->
                    old.copy(isLoading = true, isPaging = false, bands = emptyList(), page = 1, errorMessage = null)
                }
            } else {
                _uiState.update { old ->
                    old.copy(isLoading = false, isPaging = true, page = old.page + 1, errorMessage = null)
                }
            }
            musicRepository.getBands(searchString, _uiState.value.page).fold(
                onSuccess = { newBands ->
                    _uiState.update { old ->
                        old.copy(isLoading = false, isPaging = false, bands = old.bands + newBands)
                    }

                },
                onFailure = { error ->
                    logger.e(logTag, "Error fetching bands", error)
                    _uiState.update { old ->
                        old.copy(isLoading = false, isPaging = false, errorMessage = error.message)
                    }
                }
            )
        }
    }
}

