package dunbar.mike.mediabrowser.ui.music

sealed interface BandListIntent {
    object LoadMore : BandListIntent
    object ClearError : BandListIntent
    data class Search(val searchString: String) : BandListIntent
}