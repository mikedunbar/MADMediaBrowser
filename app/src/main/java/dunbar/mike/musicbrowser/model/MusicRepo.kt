package dunbar.mike.musicbrowser.model

import javax.inject.Inject

// todo convert to class/remove layer and just mock API layer
// todo move to a data package...follow the Android layers
interface MusicRepo {

    suspend fun getBands(): List<Band>

    suspend fun getAlbums(bandName: String): List<Album>
}

class FakeMusicRepo @Inject constructor() : MusicRepo {
    override suspend fun getBands(): List<Band> {
        return createTestBandList()
    }

    override suspend fun getAlbums(bandName: String): List<Album> {
        return createTestAlbumList(bandName)
    }
}

class RealMusicRepo @Inject constructor(
    private val api: MusicRemoteDataSource
) : MusicRepo {
    override suspend fun getBands(): List<Band> {
        return api.getBands()
    }

    override suspend fun getAlbums(bandName: String): List<Album> {
        return api.getAlbums(bandName)
    }

}

interface MusicRemoteDataSource {
    suspend fun getBands(): List<Band>

    suspend fun getAlbums(bandName: String): List<Album>
}