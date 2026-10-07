package dunbar.mike.mediabrowser.data.music

interface MusicRemoteDataSource {
    suspend fun getBands(searchString: String, startPage: Int = 1): Result<List<Band>>
    suspend fun getAlbums(bandId: String, startPage: Int = 1): Result<List<Album>>
}