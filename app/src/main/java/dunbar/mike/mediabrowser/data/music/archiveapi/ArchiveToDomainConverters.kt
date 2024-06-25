package dunbar.mike.mediabrowser.data.music.archiveapi

import dunbar.mike.mediabrowser.data.music.Album
import dunbar.mike.mediabrowser.data.music.Band
import dunbar.mike.mediabrowser.data.music.Song
import java.time.LocalDate


fun ArchiveAlbum.toDomainAlbum(band: Band): Album {
    val songList = List(metadataResponse.files.size) {
        Song(
            name = metadataResponse.files[it].name,
            durationSeconds = metadataResponse.files[it].length?.toDoubleOrNull()
        )
    }

    return Album(
        band = band,
        name = responseDoc.title,
        id = responseDoc.identifier,
        releaseDate = LocalDate.parse(metadataResponse.metadata.date),
        songs = songList,
    )
}
