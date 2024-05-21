package dunbar.mike.musicbrowser.data.archiveapi

import dunbar.mike.musicbrowser.data.Album
import dunbar.mike.musicbrowser.data.Band
import dunbar.mike.musicbrowser.data.MusicRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ArchiveApiMusicRemoteDataSource @Inject constructor(
    private val archiveApi: ArchiveApi
) : MusicRemoteDataSource {

    override suspend fun getBands(): List<Band> {
        return listOf(Band("Grateful Dead", "Rock"))
    }

    override suspend fun getAlbums(bandName: String): List<Album> {
        if (bandName != "Grateful Dead") {
            return listOf()
        }

        val showsSearchResponse =
            SuccessSearchResponse(archiveApi.searchDeadShows().searchResponsePayload)
        val shows = mutableListOf<Show>()
        showsSearchResponse.searchResponsePayload.docs.forEach { doc ->
            try {
                val showMetadata = withContext(Dispatchers.IO) {
                    archiveApi.getMetaData(doc.identifier)
                }
                val flacFiles = showMetadata.files.filter {
                    SupportedAudioFile.FLAC.ids.contains(it.format)
                }
                shows.add(
                    Show(
                        doc,
                        SuccessfulMetadataResponse(showMetadata.dir, flacFiles)
                    )
                )
            } catch (t: Throwable) {
                shows.add(Show(doc, ErrorMetadataResponse(t)))
            }
        }
        println("got shows (${shows.size}):")
        val albums = mutableListOf<Album>()
        shows.forEach { show ->
            albums.add(show.asAlbum())
            println("Title: ${show.responseDoc.title}")
            println("Date: ${show.responseDoc.date}")
            println("Avg Rating: ${show.responseDoc.avg_rating}")
            println("Web page URL: http://archive.org/details/${show.responseDoc.identifier}")
            println("Metadata URL:  https://archive.org/metadata/${show.responseDoc.identifier}")
            when (val metadataResponse = show.metadataResponse) {
                is SuccessfulMetadataResponse -> {
                    println("Files (${metadataResponse.files.size})")
                    val files = metadataResponse.files
                    files.forEach { file ->
                        println("\t\tTitle: ${file.title}, Name: http://archive.org/download/${show.responseDoc.identifier}/${file.name}, Length: ${file.length}")
                    }
                }
                is ErrorMetadataResponse -> {
                    println("Error retrieving files: ${metadataResponse.error.message}")
                    metadataResponse.error.printStackTrace()
                }
            }
        }
        return albums

    }
}