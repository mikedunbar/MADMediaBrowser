package dunbar.mike.mediabrowser.ui.music

sealed interface AlbumListIntent {
    object LoadMore : AlbumListIntent
    object ClearError : AlbumListIntent
}