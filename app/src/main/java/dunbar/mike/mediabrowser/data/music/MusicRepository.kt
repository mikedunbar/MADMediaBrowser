package dunbar.mike.mediabrowser.data.music

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MusicRepository @Inject constructor(
    private val remoteDataSource: MusicRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun getBands(searchString: String, startPage: Int): Result<List<Band>> {
        return withContext(ioDispatcher) {
            try {
                remoteDataSource.getBands(searchString, startPage)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getAlbums(band: Band): Result<List<Album>> {
        return withContext(ioDispatcher) {
            try {
                remoteDataSource.getAlbums(band)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getBand(bandId: String): Result<Band?> {
        return withContext(ioDispatcher) {
            try {
                remoteDataSource.getBand(bandId)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

}

interface MusicRemoteDataSource {
    suspend fun getBands(searchString: String, startPage: Int = 1): Result<List<Band>>

    suspend fun getBand(bandId: String): Result<Band?>

    suspend fun getAlbums(band: Band, startPage: Int = 1): Result<List<Album>>
}