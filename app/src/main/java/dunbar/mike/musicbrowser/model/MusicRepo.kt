package dunbar.mike.musicbrowser.model

import javax.inject.Inject

interface MusicRepo {

    suspend fun getBands(): List<Band>

    suspend fun getAlbums(bandName: String): List<Album>
}

interface MusicApi {
    suspend fun getBands(): List<Band>

    suspend fun getAlbums(bandName: String): List<Album>
}

interface MusicDataStore {

}

class FakeMusicRepo @Inject constructor() : MusicRepo {
    override suspend fun getBands(): List<Band> {
        return createBandList()
    }

    override suspend fun getAlbums(bandName: String): List<Album> {
        return createAlbumList(bandName)
    }
}

class RealMusicRepo @Inject constructor(
    private val api: MusicApi
) : MusicRepo {
    override suspend fun getBands(): List<Band> {
        return api.getBands()
    }

    override suspend fun getAlbums(bandName: String): List<Album> {
        return api.getAlbums(bandName)
    }

}
