package dunbar.mike.mediabrowser.data.music

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class MusicDataTests {
    private val mockWebServer = MockWebServer()

    @Test
    fun getBandsShallWorkWithBasicThreeBandSearchResponse() = runTest(testDispatcher) {
        RequestBasedDispatcher.setResponseForPath(
            path = getPathForBandSearch("East Na"),
            response = MockResponse().setBody(band1Band2Band3BandSearchResponse)
        )
        RequestBasedDispatcher.setResponseForPath(
            path = getPathForMetadataRequest(band1Id),
            response = MockResponse().setBody(band1MetadataResponse)
        )
        RequestBasedDispatcher.setResponseForPath(
            path = getPathForMetadataRequest(band2Id),
            response = MockResponse().setBody(band2MetadataResponse)
        )
        RequestBasedDispatcher.setResponseForPath(
            path = getPathForMetadataRequest(band3Id),
            response = MockResponse().setBody(band3MetadataResponse)
        )

        mockWebServer.dispatcher = RequestBasedDispatcher
        mockWebServer.start()
        val musicRepository = createMusicRepository(mockWebServer)

        val bandsResult = musicRepository.getBands("East Na", 1)

        assertTrue(bandsResult.isSuccess)
        assertEquals(
            listOf(
                band1,
                band2,
                band3,
            ), bandsResult.getOrNull()
        )
        mockWebServer.shutdown()
    }

    @Test
    fun getAlbumsShallWorkWithBasicThreeAlbumResponse() = runTest(testDispatcher) {
        RequestBasedDispatcher.setResponseForPath(
            path = getPathForAlbumSearch(band1Id),
            response = MockResponse().setBody(band1AlbumSearchResponse)
        )

        RequestBasedDispatcher.setResponseForPath(
            path = getPathForMetadataRequest(band1Album1.id),
            response = MockResponse().setBody(band1Album1MetadataResponse)
        )

        RequestBasedDispatcher.setResponseForPath(
            path = getPathForMetadataRequest(band1Album2.id),
            response = MockResponse().setBody(band1Album2MetadataResponse)
        )

        RequestBasedDispatcher.setResponseForPath(
            path = getPathForMetadataRequest(band1Album3.id),
            response = MockResponse().setBody(band1Album3MetadataResponse)
        )
        mockWebServer.dispatcher = RequestBasedDispatcher
        mockWebServer.start()
        val musicRepository = createMusicRepository(mockWebServer)

        val albumsResult = musicRepository.getAlbums(band1)

        assertTrue(albumsResult.isSuccess)
        assertEquals(
            listOf(
                band1Album1,
                band1Album2,
                band1Album3
            ), albumsResult.getOrNull()
        )
        mockWebServer.shutdown()
    }

    @Test
    fun getBandsShallParallelizeBandMetadataRequests() = runTest(testDispatcher) {
        RequestBasedDispatcher.setResponseForPath(
            path = getPathForBandSearch("East Na"),
            response = MockResponse().setBody(band1Band2Band3BandSearchResponse)
        )
        RequestBasedDispatcher.setResponseForPath(
            path = getPathForMetadataRequest(band1Id),
            response = MockResponse().setBody(band1MetadataResponse)
        )
        RequestBasedDispatcher.setResponseForPath(
            path = getPathForMetadataRequest(band2Id),
            response = MockResponse().setBody(band2MetadataResponse)
        )
        RequestBasedDispatcher.setResponseForPath(
            path = getPathForMetadataRequest(band3Id),
            response = MockResponse().setBody(band3MetadataResponse)
        )

        mockWebServer.dispatcher = RequestBasedDispatcher
        mockWebServer.start()
        val musicRepository = createMusicRepository(mockWebServer)

        val start = System.currentTimeMillis()
        val bandsResult = musicRepository.getBands("East Na", 1)
        val elapsed = System.currentTimeMillis() - start

        assertTrue(bandsResult.isSuccess)
        assertTrue(elapsed < 750, "elapsed ms was $elapsed, when < 1000 is expected")
        mockWebServer.shutdown()
    }

    @Test
    fun getAlbumsShallParallelizeAlbumMetadataRequests() = runTest(testDispatcher) {
        RequestBasedDispatcher.setResponseForPath(
            path = getPathForAlbumSearch(band1Id),
            response = MockResponse().setBody(band1AlbumSearchResponse)
        )

        RequestBasedDispatcher.setResponseForPath(
            path = getPathForMetadataRequest(band1Album1.id),
            response = MockResponse().setBody(band1Album1MetadataResponse)
        )

        RequestBasedDispatcher.setResponseForPath(
            path = getPathForMetadataRequest(band1Album2.id),
            response = MockResponse().setBody(band1Album2MetadataResponse)
        )

        RequestBasedDispatcher.setResponseForPath(
            path = getPathForMetadataRequest(band1Album3.id),
            response = MockResponse().setBody(band1Album3MetadataResponse)
        )
        mockWebServer.dispatcher = RequestBasedDispatcher
        mockWebServer.start()
        val musicRepository = createMusicRepository(mockWebServer)

        val start = System.currentTimeMillis()
        val albumsResult = musicRepository.getAlbums(band1)
        val elapsed = System.currentTimeMillis() - start

        assertTrue(albumsResult.isSuccess)
        assertTrue(elapsed < 750, "elapsed ms was $elapsed, when < 1000 is expected")
        mockWebServer.shutdown()
    }
}