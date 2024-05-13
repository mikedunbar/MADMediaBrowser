package dunbar.mike.musicbrowser.ui.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dunbar.mike.musicbrowser.model.Album
import dunbar.mike.musicbrowser.model.MusicRepo
import dunbar.mike.musicbrowser.util.AndroidLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumListViewModel @Inject constructor(private val musicRepo: MusicRepo) : ViewModel() {

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