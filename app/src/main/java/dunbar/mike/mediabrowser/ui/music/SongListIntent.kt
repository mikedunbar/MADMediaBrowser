package dunbar.mike.mediabrowser.ui.music

sealed interface SongListIntent {
    object ClearError : SongListIntent
}