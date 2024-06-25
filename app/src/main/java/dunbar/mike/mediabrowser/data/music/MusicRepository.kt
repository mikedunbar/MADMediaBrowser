package dunbar.mike.mediabrowser.data.music

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MusicRepository @Inject constructor(
    private val remoteDataSource: MusicRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun getBands(searchString: String, startPage: Int): Result<List<Band>> =
        withContext(ioDispatcher) { remoteDataSource.getBands(searchString, startPage) }

    suspend fun getAlbums(band: Band) = withContext(ioDispatcher) { remoteDataSource.getAlbums(band) }

    suspend fun getBand(bandId: String) = withContext(ioDispatcher) { remoteDataSource.getBand(bandId) }

}

interface MusicRemoteDataSource {
    suspend fun getBands(searchString: String, startPage: Int = 1): Result<List<Band>>

    suspend fun getBand(bandId: String): Result<Band?>

    suspend fun getAlbums(band: Band, startPage: Int = 1): Result<List<Album>>
}