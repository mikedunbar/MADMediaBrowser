package dunbar.mike.mediabrowser.data.music

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class MusicDataTests {
    private val mockWebServer = MockWebServer()

    @Test
    fun getBandsShallWorkWithBasicThreeBandSearchResponse() = runTest(testDispatcher) {
        mockWebServer.enqueue(MockResponse().setBody(band1Band2Band3BandSearchResponse))
        mockWebServer.enqueue(MockResponse().setBody(band1MetadataResponse))
        mockWebServer.enqueue(MockResponse().setBody(band2MetadataResponse))
        mockWebServer.enqueue(MockResponse().setBody(band3MetadataResponse))
        mockWebServer.start()
        val musicRepository = createMusicRepository(mockWebServer)

        val bandsResult = musicRepository.getBands("East Na", 1)

        assertTrue(bandsResult.isSuccess)
        assertEquals(
            listOf(
                Band(band1Name, band1Description, band1Id),
                Band(band2Name, band2Description, band2Id),
                Band(band3Name, band3Description, band3Id)
            ), bandsResult.getOrNull()
        )
        mockWebServer.shutdown()
    }

    @Test
    fun getAlbumsShallWorkWithBasicThreeAlbumResponse() = runTest(testDispatcher) {
        mockWebServer.enqueue(MockResponse().setBody(band1AlbumSearchResponse))
        mockWebServer.enqueue(MockResponse().setBody(band1Album1MetadataResponse))
        mockWebServer.enqueue(MockResponse().setBody(band1Album2MetadataResponse))
        mockWebServer.enqueue(MockResponse().setBody(band1Album3MetadataResponse))
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
        mockWebServer.enqueue(MockResponse().setBody(band1Band2Band3BandSearchResponse))
        mockWebServer.enqueue(MockResponse().setBody(band1MetadataResponse).setBodyDelay(500, TimeUnit.MILLISECONDS))
        mockWebServer.enqueue(MockResponse().setBody(band2MetadataResponse).setBodyDelay(500, TimeUnit.MILLISECONDS))
        mockWebServer.enqueue(MockResponse().setBody(band3MetadataResponse).setBodyDelay(500, TimeUnit.MILLISECONDS))
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
        mockWebServer.enqueue(MockResponse().setBody(band1AlbumSearchResponse))
        mockWebServer.enqueue(MockResponse().setBody(band1Album1MetadataResponse).setBodyDelay(500, TimeUnit.MILLISECONDS))
        mockWebServer.enqueue(MockResponse().setBody(band1Album2MetadataResponse).setBodyDelay(500, TimeUnit.MILLISECONDS))
        mockWebServer.enqueue(MockResponse().setBody(band1Album3MetadataResponse).setBodyDelay(500, TimeUnit.MILLISECONDS))
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