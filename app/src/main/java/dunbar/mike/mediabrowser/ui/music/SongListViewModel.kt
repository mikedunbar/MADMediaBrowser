package dunbar.mike.mediabrowser.ui.music

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dunbar.mike.mediabrowser.data.music.MusicRepository
import dunbar.mike.mediabrowser.data.music.Song
import dunbar.mike.mediabrowser.util.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SongListViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val logger: Logger,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val logTag = "SongListViewModel"
    private val albumId: String = checkNotNull(savedStateHandle["albumId"])

    private val _uiState = MutableStateFlow(SongListUiState(isLoading = true))
    val uiState: StateFlow<SongListUiState> = _uiState.asStateFlow()

    private var fetchSongsJob: Job? = null
    private var progressJob: Job? = null

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build().apply {
        addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _uiState.update { it.copy(isPlaying = isPlaying) }
                if (isPlaying) {
                    startProgressUpdates()
                }
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    _uiState.update { it.copy(durationMs = duration.coerceAtLeast(0L)) }
                } else if (playbackState == Player.STATE_ENDED) {
                    playNext()
                }
            }
        })
    }

    init {
        fetchSongs()
    }

    fun handleIntent(intent: SongListIntent) {
        when(intent) {
            SongListIntent.ClearError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
            is SongListIntent.PlaySong -> {
                playSong(intent.song)
            }
            SongListIntent.TogglePlayPause -> {
                togglePlayPause()
            }
            is SongListIntent.SeekTo -> {
                exoPlayer.seekTo(intent.positionMs)
                _uiState.update { it.copy(currentPositionMs = intent.positionMs) }
            }
            SongListIntent.SeekForward -> {
                val newPos = (exoPlayer.currentPosition + 10000).coerceAtMost(exoPlayer.duration)
                exoPlayer.seekTo(newPos)
                _uiState.update { it.copy(currentPositionMs = newPos) }
            }
            SongListIntent.SeekBackward -> {
                val newPos = (exoPlayer.currentPosition - 10000).coerceAtLeast(0L)
                exoPlayer.seekTo(newPos)
                _uiState.update { it.copy(currentPositionMs = newPos) }
            }
            SongListIntent.PlayNext -> {
                playNext()
            }
            SongListIntent.PlayPrevious -> {
                playPrevious()
            }
        }
    }

    private fun playNext() {
        val songs = _uiState.value.songs
        val current = _uiState.value.currentPlayingSong ?: return
        val index = songs.indexOf(current)
        if (index != -1 && index + 1 < songs.size) {
            playSong(songs[index + 1])
        } else {
            _uiState.update { it.copy(isPlaying = false, currentPositionMs = 0L) }
        }
    }

    private fun playPrevious() {
        val songs = _uiState.value.songs
        val current = _uiState.value.currentPlayingSong ?: return
        val index = songs.indexOf(current)
        if (index != -1 && index > 0) {
            playSong(songs[index - 1])
        }
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (exoPlayer.isPlaying) {
                _uiState.update {
                    it.copy(
                        currentPositionMs = exoPlayer.currentPosition,
                        durationMs = exoPlayer.duration.coerceAtLeast(0L)
                    )
                }
                delay(500L.milliseconds)
            }
        }
    }

    private fun playSong(song: Song) {
        val url = "https://archive.org/download/$albumId/${song.name}"
        logger.d(logTag, "Playing song: $url")
        val mediaItem = MediaItem.fromUri(url)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
        _uiState.update { 
            it.copy(
                currentPlayingSong = song, 
                isPlaying = true,
                currentPositionMs = 0L,
                durationMs = 0L
            ) 
        }
        startProgressUpdates()
    }

    private fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            if (exoPlayer.playbackState == Player.STATE_IDLE) {
                _uiState.value.currentPlayingSong?.let { playSong(it) }
            } else {
                exoPlayer.play()
            }
        }
    }

    private fun fetchSongs() {
        fetchSongsJob?.cancel()
        fetchSongsJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, songs = emptyList()) }
            musicRepository.getSongs(albumId)
                .onSuccess { songs ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = null, songs = songs) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "Unknown error") }
                }
        }
    }

    override fun onCleared() {
        progressJob?.cancel()
        fetchSongsJob?.cancel()
        exoPlayer.release()
    }
}
