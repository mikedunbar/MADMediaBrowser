package dunbar.mike.mediabrowser.data.music.archiveapi

import dunbar.mike.mediabrowser.data.music.Band
import dunbar.mike.mediabrowser.data.music.MusicRemoteDataSource
import dunbar.mike.mediabrowser.util.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ArchiveRemoteDataSource @Inject constructor(
    private val archiveApi: ArchiveApi,
    private val logger: Logger,
    private val ioDispatcher: CoroutineDispatcher,
) : MusicRemoteDataSource {

    override suspend fun getBands(searchString: String, startPage: Int): Result<List<Band>> {
        val topLevelStart = System.currentTimeMillis()
        return withContext(ioDispatcher) {
            archiveApi.searchBands(
                rows = PAGE_SIZE,
                page = startPage,
                query = "collection:etree AND mediatype:collection AND creator:${searchString}*"
            ).let { response ->
                logger.d(TAG, "top-level search took ${System.currentTimeMillis() - topLevelStart}ms on ${Thread.currentThread().name}")
                response.body().let { responseBody ->
                    if (response.isSuccessful && responseBody != null) {
                        responseBody.response.docs
                            .map {
                                async {
                                    val innerStart = System.currentTimeMillis()
                                    logger.d(TAG, "getting band data for ${it.creator} on ${Thread.currentThread().name}")
                                    val band = getBand(it.identifier).getOrNull()
                                    logger.d(TAG, "got band data: $band for ${it.creator} in ${System.currentTimeMillis() - innerStart}ms")
                                    band
                                }
                            }
                            .awaitAll()
                            .filterNotNull()
                            .let { bandList ->
                                val end = System.currentTimeMillis()
                                logger.d(TAG, "getBands took ${end - topLevelStart}ms")
                                Result.success(bandList)
                            }
                    } else {
                        Result.failure(ArchiveApi.Exception("response code: ${response.code()}, error body: ${response.errorBody()?.string()}"))
                    }
                }
            }
        }
    }

    override suspend fun getBand(bandId: String): Result<Band?> {
        archiveApi.getMetaData(bandId).let { response ->
            response.body().let { body ->
                if (response.isSuccessful && body != null) {
                    return Result.success(Band(name = body.metadata.creator, description = body.metadata.title ?: "unknown", id = bandId))
                } else {
                    return Result.failure(ArchiveApi.Exception("response code: ${response.code()}, error body: ${response.errorBody()?.string()}"))
                }
            }
        }
    }

    override suspend fun getAlbums(band: Band, startPage: Int) =
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