package dunbar.mike.mediabrowser.data.music

interface MusicRemoteDataSource {
    suspend fun getBands(searchString: String, startPage: Int = 1): Result<List<Band>>

    suspend fun getBand(bandId: String): Result<Band?>

    suspend fun getAlbums(band: Band, startPage: Int = 1): Result<List<Album>>
}