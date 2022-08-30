package dunbar.mike.musicbrowser.model

import java.time.LocalDate

data class Album(
    val band: Band,
    val name: String,
    val releaseDate: LocalDate,
    val songs: List<Song>
)

data class Song(
    val name: String,
    val durationSeconds: Double?,
)

data class Band(
    val name: String,
    val genre: String,
)