package dunbar.mike.mediabrowser.ui.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dunbar.mike.mediabrowser.data.music.Album
import dunbar.mike.mediabrowser.data.music.MusicRepo
import dunbar.mike.mediabrowser.util.AndroidLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumListViewModel @Inject constructor(private val musicRepo: MusicRepo) : ViewModel() {
    // Add more UI state and have a loading state as default
    private val _albumList = MutableStateFlow<List<Album>>(emptyList())
    val albumList: StateFlow<List<Album>> = _albumList

    fun setBand(bandName: String) = viewModelScope.launch {
        AndroidLogger.d(TAG, "Setting bandName=%s and getting albums", bandName)
        _albumList.value = musicRepo.getAlbums(bandName)
        AndroidLogger.d(TAG, "Got album list=%s", _albumList.value)
    }

    companion object {
        private const val TAG = "AlbumListViewModel"
    }
}