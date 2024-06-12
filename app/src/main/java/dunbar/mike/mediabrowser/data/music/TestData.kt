package dunbar.mike.mediabrowser.data.music

import java.time.LocalDate


class FakeMusicRemoteDataSource : MusicRemoteDataSource {

    override suspend fun getBands() = Result.success(createTestBandList())

    override suspend fun getAlbums(bandName: String) = Result.success(createTestAlbumList(bandName))

}

val widespreadPanic = createTestBand("Widespread Panic")
val spaceWrangler = createTestAlbum(band = widespreadPanic, name = "Space Wrangler")

fun createTestBand(bandName: String): Band {
    return when {
        bandName.startsWith("Widespread Panic") -> Band(bandName, "Rock", id = bandName)
        bandName.startsWith("Drive-By Truckers") -> Band(bandName, "Rock", id = bandName)
        bandName.startsWith("Metallica") -> Band(bandName, "Heavy Metal", id = bandName)
        bandName.startsWith("Iron Maiden") -> Band(bandName, "Heavy Metal", id = bandName)
        bandName.startsWith("Outkast") -> Band(bandName, "Hip Hop", id = bandName)
        bandName.startsWith("MF Doom") -> Band(bandName, "Hip Hop", id = bandName)
        bandName.startsWith("Grateful Dead") -> Band(bandName, "Psychedelic Rock", id = bandName)
        bandName.startsWith("Phish") -> Band(bandName, "Psychedelic Rock", id = bandName)
        else -> Band("Unknown Band", "Unknown Genre", id = bandName)
    }
}

fun createTestBandList(): List<Band> {
    val bandList = mutableListOf<Band>()

    (0..50).forEach {
        bandList.addAll(
            listOf(
                createTestBand("Widespread Panic $it"),
                createTestBand("Drive-By Truckers $it"),
                createTestBand("Metallica $it"),
                createTestBand("Iron Maiden $it"),
                createTestBand("Outkast $it"),
                createTestBand("MF Doom $it"),
                createTestBand("Grateful Dead $it"),
                createTestBand("Phish $it")
            )
        )
    }
    return bandList
}

fun createTestAlbum(
    band: Band = Band("Grateful Dead", "Psychedelic Rock", id = "GratefulDead"),
    name: String = band.name,
    releaseDate: LocalDate = LocalDate.now(),
    songList: List<Song> = listOf(
        Song("$name Song 1", 300.5),
        Song("$name Song 2", 300.5),
        Song("$name Song 3", 300.5),
        Song("$name Song 4", 300.5),
        Song("$name Song 5", 300.5),
        Song("$name Song 6", 300.5),
        Song("$name Song 7", 300.5),
        Song("$name Song 8", 300.5),
        Song("$name Song 9", 300.5),
        Song("$name Song 10", 300.5),
    )
) = Album(band, name, releaseDate, songList)

fun createTestAlbumList(bandName: String): List<Album> {
    val albums = mutableListOf<Album>()
    (0..5).forEach {
        val albumName = "$bandName Album $it"
        albums.add(
            createTestAlbum(
                band = createTestBand(bandName),
                name = albumName,
                songList = listOf(
                    Song("$albumName Song 1", 300.5),
                    Song("$albumName Song 2", 300.5),
                    Song("$albumName Song 3", 300.5),
                    Song("$albumName Song 4", 300.5),
                    Song("$albumName Song 5", 300.5),
                    Song("$albumName Song 6", 300.5),
                    Song("$albumName Song 7", 300.5),
                    Song("$albumName Song 8", 300.5),
                    Song("$albumName Song 9", 300.5),
                    Song("$albumName Song 10", 300.5),
                ),
            )
        )
    }
    return albums
}

