package dunbar.mike.musicbrowser.ui

enum class Screen {
    Bands,
    Albums,
    Songs

    ;

    companion object {
        fun fromRoute(route: String?): Screen =
            when (route?.substringBefore("/")) {
                Albums.name -> Albums
                Bands.name -> Bands
                null -> Bands
                else -> throw IllegalArgumentException("Route $route not recognized")
            }
    }
}