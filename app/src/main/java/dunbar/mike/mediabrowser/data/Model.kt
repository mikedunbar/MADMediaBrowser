package dunbar.mike.mediabrowser.data

import java.time.LocalDate

data class UserData(
    val shouldHideOnboarding: Boolean,
    val darkThemeConfig: DarkThemeConfig,
)

enum class DarkThemeConfig {
    LIGHT,
    DARK,
    SYSTEM_SETTING,
}

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
    val id: String,
)