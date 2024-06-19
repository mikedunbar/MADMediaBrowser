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
        val bandSearchResponse = archiveApi.searchBands(rows = PAGE_SIZE, page = startPage)
        val bandSearchResponseBody = bandSearchResponse.body()

        if (!bandSearchResponse.isSuccessful || bandSearchResponseBody == null) {
            throw ArchiveApi.Exception(
                "Unable to getBands, response code: ${bandSearchResponse.code()}, response body: ${bandSearchResponse.errorBody()?.string()}"
            )
        }

        val bandList = bandSearchResponseBody.bandSearchResponsePayload.docs.map {
            async {
                val bandMetadata = archiveApi.getMetaData(it.identifier)
                Band(name = it.creator, description = bandMetadata.subject ?: "unknown", id = it.identifier)
            }
        }.awaitAll()
        Result.success(bandList)
    }

    override suspend fun getAlbums(bandId: String, startPage: Int): Result<List<Album>> {
        val albumSearchResponse = AlbumSearchResponse(
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
                        MetadataResponse(server = showMetadata.server, dir = showMetadata.dir, showMetadata.subject, files = flacFiles)
                    )
                )
            } catch (t: Throwable) {
                logger.e(TAG, "Error getting metadata for album ${doc.identifier}", t)
            }
        }
        val albums = mutableListOf<Album>()
        archiveAlbums.forEach {
            albums.add(it.toDomainAlbum())
        }
        return Result.success(albums)
    }

    companion object {
        const val TAG = "ArchiveRemoteDataSource"
        const val PAGE_SIZE = 20
    }
}