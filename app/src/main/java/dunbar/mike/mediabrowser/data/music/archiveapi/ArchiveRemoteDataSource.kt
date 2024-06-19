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
) : MusicRemoteDataSource {

    override suspend fun getBands(startPage: Int): Result<List<Band>> = withContext(Dispatchers.IO) {
        val response = archiveApi.searchBands(rows = PAGE_SIZE, page = startPage)
        val payload = response.body()?.bandSearchResponsePayload

        if (!response.isSuccessful || payload == null) {
            throw ArchiveApi.Exception("Unable to getBands, response code: ${response.code()}, response body: ${response.errorBody()?.string()}")
        }

        payload.docs
            .map { band ->
                async {
                    val metadata = archiveApi.getMetaData(band.identifier)
                    Band(name = band.creator, description = metadata.subject ?: "unknown", id = band.identifier)
                }
            }
            .awaitAll()
            .let { Result.success(it) }
    }

    override suspend fun getAlbums(bandId: String, startPage: Int): Result<List<Album>> = withContext(Dispatchers.IO) {
        val response = archiveApi.searchAlbums(rows = PAGE_SIZE, page = startPage, query = "collection:($bandId)")
        val payload = response.body()?.albumSearchResponsePayload

        if (!response.isSuccessful || payload == null) {
            throw ArchiveApi.Exception("Unable to getAlbums, response code: ${response.code()}, response body: ${response.errorBody()?.string()}")
        }

        payload.docs
            .map { album ->
                async {
                    val metadata = archiveApi.getMetaData(album.identifier)
                    val flacFiles = metadata.files.filter {
                        SupportedAudioFile.FLAC.ids.contains(it.format)
                    }
                    ArchiveAlbum(responseDoc = album, metadataResponse = metadata.copy(files = flacFiles))
                }
            }
            .awaitAll()
            .map { it.toDomainAlbum() }
            .let { Result.success(it) }

    }

    companion object {
        const val TAG = "ArchiveRemoteDataSource"
        const val PAGE_SIZE = 20
    }
}