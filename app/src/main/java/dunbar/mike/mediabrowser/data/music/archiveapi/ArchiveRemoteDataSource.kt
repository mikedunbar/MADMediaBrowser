package dunbar.mike.mediabrowser.data.music.archiveapi

import dunbar.mike.mediabrowser.data.music.Album
import dunbar.mike.mediabrowser.data.music.Band
import dunbar.mike.mediabrowser.data.music.MusicRemoteDataSource
import dunbar.mike.mediabrowser.util.Logger
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import kotlin.time.measureTimedValue

class ArchiveRemoteDataSource @Inject constructor(
    private val archiveApi: ArchiveApi,
    private val logger: Logger,
) : MusicRemoteDataSource {

    override suspend fun getBands(searchString: String, startPage: Int): Result<List<Band>> = try {
        logger.d(TAG, "getBands: searchString = $searchString, startPage = $startPage")
        val (response, duration) = measureTimedValue {
            archiveApi.searchBands(
                rows = PAGE_SIZE,
                page = startPage,
                query = "collection:etree AND mediatype:collection AND creator:(${searchString}*)"
            )
        }
        logger.d(TAG, "getBands: search took $duration")

        val responseBody = response.body()
        if (response.isSuccessful && responseBody != null) {
            val bands = responseBody.response.docs.map { doc ->
                Band(name = doc.creator, description = doc.title ?: "No description", id = doc.identifier)
            }
            logger.d(TAG, "Returning ${bands.size} bands from search query")
            Result.success(bands)
        } else {
            val code = response.code()
            val errorBody = response.errorBody()?.string()
            logger.e(TAG, "getBands: failed: $code: $errorBody")
            Result.failure(Exception("$code: $errorBody"))
        }
    } catch (e: Exception) {
        logger.e(TAG, "getBands: failed with exception: ${e.message}")
        Result.failure(e)
    }

    override suspend fun getAlbums(bandId: String, startPage: Int): Result<List<Album>> = try {
        val (albums, duration) = measureTimedValue {
            archiveApi.searchAlbums(rows = PAGE_SIZE, page = startPage, query = "collection:($bandId)")
        }
        logger.d(TAG, "getAlbums: bandId = $bandId, albums search took $duration")


        val (result, totalMetaDataDuration) = measureTimedValue {
            albums.let { response ->
                response.body().let { body ->
                    if (response.isSuccessful && body != null) {
                        coroutineScope {
                            body.response.docs
                                .map { doc ->
                                    async {
                                        val (metadata, metadataDuration) = measureTimedValue { archiveApi.getMetaData(doc.identifier) }
                                        logger.d(TAG, "getAlbums: metadata search for ${doc.identifier} took $metadataDuration")
                                        metadata.body()?.let { metadata ->
                                            val flacFiles = metadata.files.filter {
                                                SupportedAudioFile.FLAC.ids.contains(it.format)
                                            }
                                            ArchiveAlbum(responseDoc = doc, metadataResponse = metadata.copy(files = flacFiles))
                                        }
                                    }
                                }
                                .awaitAll()
                                .filterNotNull()
                                .map { it.toDomainAlbum(bandId) }
                                .let { Result.success(it) }
                        }
                    } else {
                        Result.failure(ArchiveApi.Exception("response code: ${response.code()}, response body: ${response.errorBody()?.string()}"))
                    }
                }
            }
        }
        logger.d(TAG, "getAlbums: bandId = $bandId, all metadata searches took $totalMetaDataDuration")
        result
    } catch (e: Exception) {
        logger.e(TAG, "getAlbums: failed with exception: ${e.message}")
        Result.failure(e)
    }


    companion object {
        const val PAGE_SIZE = 20
        const val TAG = "ArchiveRemoteDataSource"
    }
}