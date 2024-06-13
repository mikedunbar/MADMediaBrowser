package dunbar.mike.mediabrowser.data.music.archiveapi

import dunbar.mike.mediabrowser.data.music.Album
import dunbar.mike.mediabrowser.data.music.Band
import dunbar.mike.mediabrowser.data.music.MusicRemoteDataSource
import dunbar.mike.mediabrowser.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ArchiveRemoteDataSource @Inject constructor(
    private val archiveApi: ArchiveApi,
    private val logger: Logger,
) : MusicRemoteDataSource {

    override suspend fun getBands(startPage: Int): Result<List<Band>> = withContext(Dispatchers.IO) {
        logger.d(TAG, "Getting bands from page $startPage")
        val bandSearchResponse = archiveApi.searchBands(rows = PAGE_SIZE, page = startPage)
        val bandSearchResponseBody = bandSearchResponse.body()

        if (!bandSearchResponse.isSuccessful || bandSearchResponseBody == null) {
            throw ArchiveApi.Exception(
                "Unable to getBands, response code: ${bandSearchResponse.code()}, response body: ${bandSearchResponse.errorBody()?.string()}"
            )
        }
        val bands = mutableListOf<Band>()
        val bandSearchResponseList = bandSearchResponseBody.bandSearchResponsePayload.docs
        bandSearchResponseList.map {
            async {
                val bandMetadata = getMetaData(it.identifier)
                bands.add(Band(name = it.creator, description = bandMetadata.subject ?: "unknown", id = it.identifier))
            }
        }.awaitAll()
        logger.d(TAG, "Returning bands from page=%d: %s", startPage, bands)
        Result.success(bands)
    }

    private suspend fun getMetaData(identifier: String): SuccessfulMetadataResponse {
//        logger.d(TAG, "Getting metadata for $identifier")
        val metadata = archiveApi.getMetaData(identifier)
//        logger.d(TAG, "Got metadata $identifier: $metadata")
        return metadata
    }

    override suspend fun getAlbums(bandId: String, startPage: Int): Result<List<Album>> {
        val showsSearchResponse = ShowSearchSuccessResponse(archiveApi.searchAlbums(
            rows = PAGE_SIZE,
            page = startPage,
            query = "collection:($bandId)"
        ).showSearchResponsePayload)
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
                        SuccessfulMetadataResponse(server = showMetadata.server, dir = showMetadata.dir, showMetadata.subject, files = flacFiles)
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
        const val TAG = "ArchiveRemoteDataSource"
        const val PAGE_SIZE = 20
    }
}