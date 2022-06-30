package dunbar.mike.musicbrowser.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dunbar.mike.musicbrowser.model.Album
import dunbar.mike.musicbrowser.model.MusicRepo
import dunbar.mike.musicbrowser.util.AndroidLogger
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumListViewModel @Inject constructor(
    private val musicRepo: MusicRepo
) : ViewModel() {
    private val _albumList = MutableLiveData<List<Album>>(emptyList())

    val albumList: LiveData<List<Album>> = _albumList

    fun setBand(bandName: String) {
        AndroidLogger.d(TAG, "Setting bandName=%s and getting albums", bandName)

        viewModelScope.launch {
            val fetchedList = async {
                musicRepo.getAlbums(bandName)
            }
            _albumList.value = fetchedList.await()
            AndroidLogger.d(TAG, "Got album list=%s", _albumList.value)
        }

    }

    companion object {
        private const val TAG = "AlbumListViewModel"
    }
}