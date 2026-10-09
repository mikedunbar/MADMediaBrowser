package dunbar.mike.mediabrowser.ui.music

import dunbar.mike.mediabrowser.data.music.Band

data class BandListUiState(
    val searchString: String = "",
    val page: Int = 1,
    val bands: List<Band> = emptyList(),
    val isLoading: Boolean = false,
    val isPaging: Boolean = false,
    val errorMessage: String? = null,
)