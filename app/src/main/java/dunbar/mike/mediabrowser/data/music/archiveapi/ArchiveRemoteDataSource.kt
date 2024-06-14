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
        val albumSearchResponse = AlbumSearchSuccessResponse(
            archiveApi.searchAlbums(
                rows = PAGE_SIZE,
                page = startPage,
                query = "collection:($bandId)"
            ).albumSearchResponsePayload
        )
        val archiveAlbums = mutableListOf<ArchiveAlbum>()
        albumSearchResponse.albumSearchResponsePayload.docs.forEach { doc ->
            try {
                val showMetadata = withContext(Dispatchers.IO) {
                    archiveApi.getMetaData(doc.identifier)
                }
                val flacFiles = showMetadata.files.filter {
                    SupportedAudioFile.FLAC.ids.contains(it.format)
                }
                archiveAlbums.add(
                    ArchiveAlbum(
                        doc,
                        SuccessfulMetadataResponse(server = showMetadata.server, dir = showMetadata.dir, showMetadata.subject, files = flacFiles)
                    )
                )
            } catch (t: Throwable) {
                logger.e(TAG, "Error getting metadata for album ${doc.identifier}", t)
            }
        }
        println("got albums (${archiveAlbums.size}):")
        val albums = mutableListOf<Album>()
        archiveAlbums.forEach {
            albums.add(it.asAlbum())
            println("Title: ${it.responseDoc.title}")
            println("Date: ${it.responseDoc.date}")
            println("Avg Rating: ${it.responseDoc.avg_rating}")
            println("Web page URL: http://archive.org/details/${it.responseDoc.identifier}")
            println("Metadata URL:  https://archive.org/metadata/${it.responseDoc.identifier}")
            when (it.metadataResponse) {
                is SuccessfulMetadataResponse -> {
                    logger.d(TAG, "Files (${it.metadataResponse.files.size})")
                    val files = it.metadataResponse.files
                    files.forEach { file ->
                        logger.d(TAG, "\t\tTitle: ${file.title}, Name: http://archive.org/download/${it.responseDoc.identifier}/${file.name}, Length: ${file.length}")
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