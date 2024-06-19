package dunbar.mike.mediabrowser.data.music.archiveapi

import dunbar.mike.mediabrowser.data.music.Album
import dunbar.mike.mediabrowser.data.music.Band
import dunbar.mike.mediabrowser.data.music.MusicRemoteDataSource
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
        val payload = response.body()?.response

        if (!response.isSuccessful || payload == null) {
            throw ArchiveApi.Exception("Unable to getBands, response code: ${response.code()}, response body: ${response.errorBody()?.string()}")
        }

        payload.docs
            .map { band ->
                async {
                    val subject: String = archiveApi.getMetaData(band.identifier).body()?.subject ?: "unknown"
                    Band(name = band.creator, description = subject, id = band.identifier)
                }
            }
            .awaitAll()
            .let { Result.success(it) }
    }

    override suspend fun getAlbums(bandId: String, startPage: Int): Result<List<Album>> = withContext(Dispatchers.IO) {
        val response = archiveApi.searchAlbums(rows = PAGE_SIZE, page = startPage, query = "collection:($bandId)")
        val payload = response.body()?.response

        if (!response.isSuccessful || payload == null) {
            throw ArchiveApi.Exception("Unable to getAlbums, response code: ${response.code()}, response body: ${response.errorBody()?.string()}")
        }

        payload.docs
            .map { album ->
                async {
                    val metadataResponse = archiveApi.getMetaData(album.identifier)
                    val metadata = metadataResponse.body()
                    if (metadataResponse.isSuccessful && metadata != null) {
                        val flacFiles = metadata.files.filter {
                            SupportedAudioFile.FLAC.ids.contains(it.format)
                        }
                        ArchiveAlbum(responseDoc = album, metadataResponse = metadata.copy(files = flacFiles))
                    } else {
                        null
                    }

                }
            }
            .awaitAll()
            .filterNotNull()
            .map { it.toDomainAlbum() }
            .let { Result.success(it) }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}