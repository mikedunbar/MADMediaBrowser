package dunbar.mike.mediabrowser.data.music.archiveapi

import dunbar.mike.mediabrowser.data.music.Album
import dunbar.mike.mediabrowser.data.music.Song
import java.time.LocalDate


fun ArchiveAlbum.toDomainAlbum(bandId: String) = Album(
    bandId = bandId,
    name = responseDoc.title,
    id = responseDoc.identifier,
    releaseDate = LocalDate.parse(metadataResponse.metadata.date),
    songs = metadataResponse.toSongList(),
)

fun MetadataResponse.toSongList(): List<Song> = List(this.files.size) {
    Song(
        name = this.files[it].name,
        durationSeconds = this.files[it].length?.toDoubleOrNull()
    )
}