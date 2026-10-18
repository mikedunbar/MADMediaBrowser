package dunbar.mike.mediabrowser.ui.music

import dunbar.mike.mediabrowser.data.music.Song

sealed interface SongListIntent {
    object ClearError : SongListIntent
    data class PlaySong(val song: Song) : SongListIntent
    object TogglePlayPause : SongListIntent
}
