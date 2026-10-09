package dunbar.mike.mediabrowser.data.music

import javax.inject.Inject

class MusicRepository @Inject constructor(private val remoteDataSource: MusicRemoteDataSource) {
    suspend fun getBands(searchString: String, startPage: Int): Result<List<Band>> = remoteDataSource.getBands(searchString, startPage)
    suspend fun getAlbums(bandId: String, startPage: Int): Result<List<Album>> = remoteDataSource.getAlbums(bandId, startPage)
}