package dunbar.mike.mediabrowser.data.music.archiveapi

import dunbar.mike.mediabrowser.data.music.Album
import dunbar.mike.mediabrowser.data.music.Band
import dunbar.mike.mediabrowser.data.music.Song
import dunbar.mike.mediabrowser.util.localDateTimeFromIsoInstant


fun ArchiveAlbum.toDomainAlbum(): Album {
    val songList = List(metadataResponse.files.size) {
        Song(
            name = metadataResponse.files[it].title ?: "unknown",
            durationSeconds = metadataResponse.files[it].length?.toDoubleOrNull()
        )
    }

    return Album(
        band = Band(
            name = responseDoc.creator,
            description = "Rock",
            id = responseDoc.identifier,
        ),
        name = responseDoc.title,
        id = responseDoc.identifier,
        releaseDate = localDateTimeFromIsoInstant(responseDoc.date).toLocalDate(),
        songs = songList,
    )
}
