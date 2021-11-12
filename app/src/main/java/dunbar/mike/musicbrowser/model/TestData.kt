package dunbar.mike.musicbrowser.model

import java.time.LocalDate

fun createBandList(): List<Band> {
    val bandList = mutableListOf<Band>()

    (0..50).forEach {
        bandList.addAll(
            listOf(
                getBandInfo("Widespread Panic $it"),
                getBandInfo("Drive-By Truckers $it"),
                getBandInfo("Metallica $it"),
                getBandInfo("Iron Maiden $it"),
                getBandInfo("Outkast $it"),
                getBandInfo("MF Doom $it"),
                getBandInfo("Grateful Dead $it"),
                getBandInfo("Phish $it")
            )
        )
    }
    return bandList
}

fun getBandInfo(bandName: String): Band {
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

fun createAlbumInfo(
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

fun createAlbumList(bandName: String): List<Album> {
    val albums = mutableListOf<Album>()
    (0..5).forEach {
        val name = "$bandName Album $it"
        albums.add(
            createAlbumInfo(
                band = getBandInfo(bandName),
                name = name,
                songList = listOf(
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
                ),
            )
        )
    }
    return albums
}

