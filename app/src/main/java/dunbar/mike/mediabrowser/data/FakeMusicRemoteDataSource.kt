package dunbar.mike.mediabrowser.data

import java.time.LocalDate


class FakeMusicRemoteDataSource: MusicRemoteDataSource {
    // todo delay for a bit, to simulate network delay
    override suspend fun getBands(): List<Band>  = createTestBandList()

    override suspend fun getAlbums(bandName: String) = createTestAlbumList(bandName)

}

fun createTestBandList(): List<Band> {
    val bandList = mutableListOf<Band>()

    (0..50).forEach {
        bandList.addAll(
            listOf(
                createTestBandInfo("Widespread Panic $it"),
                createTestBandInfo("Drive-By Truckers $it"),
                createTestBandInfo("Metallica $it"),
                createTestBandInfo("Iron Maiden $it"),
                createTestBandInfo("Outkast $it"),
                createTestBandInfo("MF Doom $it"),
                createTestBandInfo("Grateful Dead $it"),
                createTestBandInfo("Phish $it")
            )
        )
    }
    return bandList
}

fun createTestBandInfo(bandName: String): Band {
    return when {
        bandName.startsWith("Widespread Panic") -> Band(bandName, "Rock")
        bandName.startsWith("Drive-By Truckers") -> Band(bandName, "Rock")
        bandName.startsWith("Metallica") -> Band(bandName, "Heavy Metal")
        bandName.startsWith("Iron Maiden") -> Band(bandName, "Heavy Metal")
        bandName.startsWith("Outkast") -> Band(bandName, "Hip Hop")
        bandName.startsWith("MF Doom") -> Band(bandName, "Hip Hop")
        bandName.startsWith("Grateful Dead") -> Band(bandName, "Psychedelic Rock")
        bandName.startsWith("Phish") -> Band(bandName, "Psychedelic Rock")
        else -> Band("Unknown Band", "Unknown Genre")
    }
}

fun createTestAlbumInfo(
    band: Band = Band("Grateful Dead", "Psychedelic Rock"),
    name: String = band.name,
    releaseDate: LocalDate = LocalDate.now(),
    songList: List<Song> = listOf(
        Song("Friend of the Devil", 201.5)
    )
) = Album(
    band,
    name,
    releaseDate,
    songList,
)

fun createTestAlbumList(bandName: String): List<Album> {
    val albums = mutableListOf<Album>()
    (0..5).forEach {
        val albumName = "$bandName Album $it"
        albums.add(
            createTestAlbumInfo(
                band = createTestBandInfo(bandName),
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

