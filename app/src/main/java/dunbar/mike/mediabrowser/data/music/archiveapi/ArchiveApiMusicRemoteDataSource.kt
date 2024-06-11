package dunbar.mike.mediabrowser.data.music.archiveapi

import dunbar.mike.mediabrowser.data.music.Album
import dunbar.mike.mediabrowser.data.music.Band
import dunbar.mike.mediabrowser.data.music.MusicRemoteDataSource
import dunbar.mike.mediabrowser.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ArchiveApiMusicRemoteDataSource @Inject constructor(
    private val archiveApi: ArchiveApi,
    private val logger: Logger,
) : MusicRemoteDataSource {

    override suspend fun getBands() = withContext(Dispatchers.IO) {
        val bandSearchResponse = archiveApi.searchBands()
        val responseBody = bandSearchResponse.body()

        if (!bandSearchResponse.isSuccessful || responseBody == null) {
            throw ArchiveApi.Exception(
                "Unable to getBands, response code: ${bandSearchResponse.code()}, response body: ${bandSearchResponse.errorBody()?.string()}"
            )
        }

        val bands = mutableListOf<Band>()
        responseBody.bandSearchResponsePayload.docs.forEach { doc ->

            val bandMetadata = async {
                getMetaData(doc.creator, doc.identifier)
            }

            bands.add(
                Band(
                    name = doc.creator,
                    genre = "Unknown",
                    id = doc.identifier
                )
            )
        }
        logger.d(TAG, "Done processing payload")
        Result.success(bands)
    }

    private suspend fun getMetaData(creator: String, identifier: String): SuccessfulMetadataResponse {
        logger.d(TAG, "Getting metadata for band $creator with ID $identifier: $identifier")
        val bandMetadata = archiveApi.getMetaData(identifier)
        logger.d(TAG, "Got metadata for band $creator: $bandMetadata")
        return bandMetadata
    }

    override suspend fun getAlbums(bandName: String): Result<List<Album>> {
        if (bandName != "Grateful Dead") {
            return Result.success(listOf())
        }

        val showsSearchResponse = ShowSearchSuccessResponse(archiveApi.searchDeadShows().showSearchResponsePayload)
        val shows = mutableListOf<Show>()
        showsSearchResponse.showSearchResponsePayload.docs.forEach { doc ->
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
                        SuccessfulMetadataResponse(server = showMetadata.server, dir = showMetadata.dir, files = flacFiles)
                    )
                )
            } catch (t: Throwable) {
                logger.e(TAG, "Error getting metadata for show ${doc.identifier}", t)
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
            when (show.metadataResponse) {
                is SuccessfulMetadataResponse -> {
                    println("Files (${show.metadataResponse.files.size})")
                    val files = show.metadataResponse.files
                    files.forEach { file ->
                        println("\t\tTitle: ${file.title}, Name: http://archive.org/download/${show.responseDoc.identifier}/${file.name}, Length: ${file.length}")
                    }
                }
            }
        }
        return Result.success(albums)

    }

    companion object {
        const val TAG = "ArchiveApiMusicRemoteDataSource"
    }
}