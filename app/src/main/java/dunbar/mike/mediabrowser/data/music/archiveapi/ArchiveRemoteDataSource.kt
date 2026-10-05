package dunbar.mike.mediabrowser.data.music.archiveapi

import dunbar.mike.mediabrowser.data.music.Album
import dunbar.mike.mediabrowser.data.music.Band
import dunbar.mike.mediabrowser.data.music.MusicRemoteDataSource
import dunbar.mike.mediabrowser.util.Logger
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class ArchiveRemoteDataSource @Inject constructor(
    private val archiveApi: ArchiveApi,
    private val logger: Logger,
) : MusicRemoteDataSource {

    override suspend fun getBands(searchString: String, startPage: Int): Result<List<Band>> {
        return try {
            val topLevelStart = System.currentTimeMillis()
            logger.d(TAG, "getBands: searchString = $searchString, startPage = $startPage")

            val response = archiveApi.searchBands(
                rows = PAGE_SIZE,
                page = startPage,
                query = "collection:etree AND mediatype:collection AND creator:(${searchString}*)"
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
                logger.d(TAG, "Emitted ${initialBands.size} bands from search query")
                Result.success(initialBands)
            } else {
                val code = response.code()
                val errorBody = response.errorBody()?.string()
                logger.e(TAG, "Search failed: $code: $errorBody")
                Result.failure(Exception("$code: $errorBody"))
            }
        } catch (e: Exception) {
            logger.e(TAG, "getBands failed with exception", e)
            Result.failure(e)
        }
    }

    override suspend fun getBand(bandId: String): Result<Band?> {
        return try {
            archiveApi.getMetaData(bandId).let { response ->
                response.body().let { body ->
                    if (response.isSuccessful && body != null) {
                        Result.success(Band(name = body.metadata.creator, description = body.metadata.title ?: "unknown", id = bandId))
                    } else {
                        Result.failure(ArchiveApi.Exception("response code: ${response.code()}, error body: ${response.errorBody()?.string()}"))
                    }
                }
            }
        } catch (e: Exception) {
            logger.e(TAG, "getBand failed with exception", e)
            Result.failure(e)
        }
    }

    override suspend fun getAlbums(band: Band, startPage: Int): Result<List<Album>> {
        return try {
            archiveApi.searchAlbums(rows = PAGE_SIZE, page = startPage, query = "collection:(${band.id})").let { response ->
                response.body().let { body ->
                    if (response.isSuccessful && body != null) {
                        coroutineScope {
                            body.response.docs
                                .map { doc ->
                                    async {
                                        logger.d(TAG, "getting metadata for album ${doc.identifier}")
                                        archiveApi.getMetaData(doc.identifier).body()?.let { metadata ->
                                            val flacFiles = metadata.files.filter {
                                                SupportedAudioFile.FLAC.ids.contains(it.format)
                                            }
                                            ArchiveAlbum(responseDoc = doc, metadataResponse = metadata.copy(files = flacFiles))
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
        } catch (e: Exception) {
            logger.e(TAG, "getAlbums failed with exception", e)
            Result.failure(e)
        }
    }

    companion object {
        const val PAGE_SIZE = 20
        const val TAG = "ArchiveRemoteDataSource"
    }
}