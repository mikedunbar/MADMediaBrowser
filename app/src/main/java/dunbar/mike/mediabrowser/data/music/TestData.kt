package dunbar.mike.mediabrowser.data.music

import java.time.LocalDate


@Suppress("unused") // Used by manually updating the Hilt module
class FakeMusicRemoteDataSource : MusicRemoteDataSource {

    override suspend fun getBands(searchString: String, startPage: Int): Result<List<Band>> = Result.success(createTestBandList())

    override suspend fun getAlbums(bandId: String, startPage: Int) = Result.success(createTestAlbumList(bandId))

}

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
    bandId: String = "Grateful Dead",
    albumId: String = "Aoxamoa",
    name: String = "Grateful Dead",
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
) = Album(bandId, name, albumId, releaseDate, songList)

fun createTestAlbumList(bandId: String): List<Album> {
    val albums = mutableListOf<Album>()
    (0..5).forEach {
        val albumName = "$bandId} Album $it"
        albums.add(
            createTestAlbum(
                bandId = bandId,
                name = albumName,
                albumId = "blah",
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
