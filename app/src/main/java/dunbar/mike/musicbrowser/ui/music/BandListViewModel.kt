package dunbar.mike.musicbrowser.ui.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dunbar.mike.musicbrowser.model.Band
import dunbar.mike.musicbrowser.model.MusicRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BandListViewModel @Inject constructor(private val musicRepo: MusicRepo) : ViewModel() {

    private val _bandList = MutableStateFlow<List<Band>>(emptyList())
    val bandList: StateFlow<List<Band>> = _bandList

    init {
        viewModelScope.launch {
            _bandList.value = musicRepo.getBands()
        }
    }
}