package dunbar.mike.mediabrowser.data.music.archiveapi

import dunbar.mike.mediabrowser.data.music.Album
import dunbar.mike.mediabrowser.data.music.Band
import dunbar.mike.mediabrowser.data.music.MusicRemoteDataSource
import dunbar.mike.mediabrowser.util.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ArchiveRemoteDataSource @Inject constructor(
    private val archiveApi: ArchiveApi,
    private val logger: Logger,
    private val ioDispatcher: CoroutineDispatcher,
) : MusicRemoteDataSource {

    override fun getBands(searchString: String, startPage: Int): Flow<List<Band>> = flow {
        val topLevelStart = System.currentTimeMillis()
        logger.d(TAG, "getBands on ${Thread.currentThread().name}")

        val response = archiveApi.searchBands(
            rows = PAGE_SIZE,
            page = startPage,
            query = "collection:etree AND mediatype:collection AND creator:${searchString}*"
        )

        logger.d(TAG, "top-level search took ${System.currentTimeMillis() - topLevelStart}ms")

        val responseBody = response.body()
        if (response.isSuccessful && responseBody != null) {
            val initialBands = responseBody.response.docs.map { doc ->
                Band(
                    name = doc.creator,
                    description = doc.title ?: "No description",
                    id = doc.identifier
                )
            }
            // Emit the results immediately from the search query (fast path)
            emit(initialBands)
            logger.d(TAG, "Emitted ${initialBands.size} bands from search query")
        } else {
            logger.e(TAG, "Search failed: ${response.code()} ${response.errorBody()?.string()}")
            emit(emptyList())
        }
    }

    override suspend fun getBand(bandId: String): Result<Band?> {
        archiveApi.getMetaData(bandId).let { response ->
            response.body().let { body ->
                return if (response.isSuccessful && body != null) {
                    Result.success(Band(name = body.metadata.creator, description = body.metadata.title ?: "unknown", id = bandId))
                } else {
                    Result.failure(ArchiveApi.Exception("response code: ${response.code()}, error body: ${response.errorBody()?.string()}"))
                }
            }
        }
    }

    override suspend fun getAlbums(band: Band, startPage: Int): Result<List<Album>> =
        archiveApi.searchAlbums(rows = PAGE_SIZE, page = startPage, query = "collection:(${band.id})").let { response ->
            response.body().let { body ->
                if (response.isSuccessful && body != null) {
                    withContext(ioDispatcher) {
                        body.response.docs
                            .map {
                                async {
                                    logger.d(TAG, "getting metadata for album ${it.identifier}")
                                    archiveApi.getMetaData(it.identifier).body()?.let { metadata ->
                                        val flacFiles = metadata.files.filter {
                                            SupportedAudioFile.FLAC.ids.contains(it.format)
                                        }
                                        ArchiveAlbum(responseDoc = it, metadataResponse = metadata.copy(files = flacFiles))
                                    }
                                }
                            }
                            .awaitAll()
                            .filterNotNull()
                            .map { it.toDomainAlbum(band) }
                            .let { Result.success(it) }
                    }
                } else {
                    Result.failure(ArchiveApi.Exception("response code: ${response.code()}, response body: ${response.errorBody()?.string()}"))
                }
            }
        }

    companion object {
        const val PAGE_SIZE = 20
        const val TAG = "ArchiveRemoteDataSource"
    }
}