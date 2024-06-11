package dunbar.mike.mediabrowser.data.music

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MusicRepo @Inject constructor(
    private val remoteDataSource: MusicRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) {
    // todo Let's get an infinite scroll working here
    suspend fun getBands(): Result<List<Band>> = withContext(ioDispatcher) { remoteDataSource.getBands() }

    suspend fun getAlbums(bandName: String) = withContext(ioDispatcher) { remoteDataSource.getAlbums(bandName) }

}

interface MusicRemoteDataSource {
    suspend fun getBands(): Result<List<Band>>

    suspend fun getAlbums(bandName: String): Result<List<Album>>
}