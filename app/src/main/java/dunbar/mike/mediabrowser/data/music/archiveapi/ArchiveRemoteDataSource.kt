package dunbar.mike.mediabrowser.data.music.archiveapi

import dunbar.mike.mediabrowser.data.music.Band
import dunbar.mike.mediabrowser.data.music.MusicRemoteDataSource
import dunbar.mike.mediabrowser.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ArchiveRemoteDataSource @Inject constructor(private val archiveApi: ArchiveApi, private val logger: Logger) : MusicRemoteDataSource {

    override suspend fun getBands(startPage: Int) = archiveApi.searchBands(rows = PAGE_SIZE, page = startPage).let { response ->
            response.body().let { responseBody ->
                if (response.isSuccessful && responseBody != null) {
                    val start = System.currentTimeMillis()
                    responseBody.response.docs
                        .map {
                            withContext(Dispatchers.IO) {
                                async {
                                    logger.d(TAG, "getting metadata for ${it.creator}")
                                    val subject: String = archiveApi.getMetaData(it.identifier).body()?.subject ?: "unknown"
                                    logger.d(TAG, "got metadata for ${it.creator}")
                                    Band(name = it.creator, description = subject, id = it.identifier)
                                }
                            }
                        }
                        .awaitAll()
                        .let { band ->
                            val end = System.currentTimeMillis()
                            logger.d(TAG, "getBands took ${end - start}ms")
                            Result.success(band)
                        }

                } else {
                    Result.failure(ArchiveApi.Exception("response code: ${response.code()}, error body: ${response.errorBody()?.string()}"))
                }
            }
    }

    override suspend fun getAlbums(bandId: String, startPage: Int) =
        archiveApi.searchAlbums(rows = PAGE_SIZE, page = startPage, query = "collection:($bandId)").let { response ->
            response.body().let { body ->
                if (response.isSuccessful && body != null) {
                    withContext(Dispatchers.IO) {
                        body.response.docs
                            .map {
                                async {
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
                            .map { it.toDomainAlbum() }
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