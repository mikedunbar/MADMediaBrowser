package dunbar.mike.mediabrowser.data.music

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
    val description: String,
    val id: String,
)