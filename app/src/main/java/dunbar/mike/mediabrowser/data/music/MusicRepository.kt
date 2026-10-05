package dunbar.mike.mediabrowser.data.music

import dunbar.mike.mediabrowser.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MusicRepository @Inject constructor(
    private val remoteDataSource: MusicRemoteDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun getBands(searchString: String, startPage: Int): Result<List<Band>> = withContext(ioDispatcher) {
        try {
            remoteDataSource.getBands(searchString, startPage)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAlbums(band: Band): Result<List<Album>> = withContext(ioDispatcher) {
        try {
            remoteDataSource.getAlbums(band)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBand(bandId: String): Result<Band?> = withContext(ioDispatcher) {
        try {
            remoteDataSource.getBand(bandId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}