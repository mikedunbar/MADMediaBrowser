package dunbar.mike.mediabrowser.data.music

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MusicRepository @Inject constructor(
    private val remoteDataSource: MusicRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun getBands(startPage: Int): Result<List<Band>> = withContext(ioDispatcher) {
        remoteDataSource.getBands(startPage)
    }

    suspend fun getAlbums(bandName: String) = withContext(ioDispatcher) { remoteDataSource.getAlbums(bandName) }

}

interface MusicRemoteDataSource {
    suspend fun getBands(startPage: Int = 1): Result<List<Band>>

    suspend fun getAlbums(bandId: String, startPage: Int = 1): Result<List<Album>>
}