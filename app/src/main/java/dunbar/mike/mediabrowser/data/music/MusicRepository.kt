package dunbar.mike.mediabrowser.data.music

import javax.inject.Inject

class MusicRepository @Inject constructor(private val remoteDataSource: MusicRemoteDataSource) {
    suspend fun getBands(searchString: String, startPage: Int): Result<List<Band>> = remoteDataSource.getBands(searchString, startPage)
    suspend fun getAlbums(band: Band): Result<List<Album>> = remoteDataSource.getAlbums(band)
    suspend fun getBand(bandId: String): Result<Band?> = remoteDataSource.getBand(bandId)
}