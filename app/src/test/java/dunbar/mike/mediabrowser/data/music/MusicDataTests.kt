package dunbar.mike.mediabrowser.data.music

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.net.SocketTimeoutException

class MusicDataTests {
    private val mockWebServer = MockWebServer()

    @Test
    fun getBandsShallWorkWithBasicThreeBandSearchResponse() = runTest(testDispatcher) {
        RequestBasedDispatcher.setResponseForRequest(
            path = getPathForBandSearch("East Na"),
            response = MockResponse().setBody(band1Band2Band3BandSearchResponse)
        )
        RequestBasedDispatcher.setResponseForRequest(
            path = getPathForMetadataRequest(band1Id),
            response = MockResponse().setBody(band1MetadataResponse)
        )
        RequestBasedDispatcher.setResponseForRequest(
            path = getPathForMetadataRequest(band2Id),
            response = MockResponse().setBody(band2MetadataResponse)
        )
        RequestBasedDispatcher.setResponseForRequest(
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
        RequestBasedDispatcher.setResponseForRequest(
            path = getPathForAlbumSearch(band1Id),
            response = MockResponse().setBody(band1AlbumSearchResponse)
        )

        RequestBasedDispatcher.setResponseForRequest(
            path = getPathForMetadataRequest(band1Album1.id),
            response = MockResponse().setBody(band1Album1MetadataResponse)
        )

        RequestBasedDispatcher.setResponseForRequest(
            path = getPathForMetadataRequest(band1Album2.id),
            response = MockResponse().setBody(band1Album2MetadataResponse)
        )

        RequestBasedDispatcher.setResponseForRequest(
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
        RequestBasedDispatcher.setResponseForRequest(
            path = getPathForBandSearch("East Na"),
            response = MockResponse().setBody(band1Band2Band3BandSearchResponse)
        )
        RequestBasedDispatcher.setResponseForRequest(
            path = getPathForMetadataRequest(band1Id),
            response = MockResponse().setBody(band1MetadataResponse)
        )
        RequestBasedDispatcher.setResponseForRequest(
            path = getPathForMetadataRequest(band2Id),
            response = MockResponse().setBody(band2MetadataResponse)
        )
        RequestBasedDispatcher.setResponseForRequest(
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
        RequestBasedDispatcher.setResponseForRequest(
            path = getPathForAlbumSearch(band1Id),
            response = MockResponse().setBody(band1AlbumSearchResponse)
        )

        RequestBasedDispatcher.setResponseForRequest(
            path = getPathForMetadataRequest(band1Album1.id),
            response = MockResponse().setBody(band1Album1MetadataResponse)
        )

        RequestBasedDispatcher.setResponseForRequest(
            path = getPathForMetadataRequest(band1Album2.id),
            response = MockResponse().setBody(band1Album2MetadataResponse)
        )

        RequestBasedDispatcher.setResponseForRequest(
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

    @Test
    fun getAlbumsShallReturnFailureForSocketTimeoutAndNotCrashApp() = runTest(testDispatcher){
        RequestBasedDispatcher.setResponseForRequest(
            path = getPathForAlbumSearch(band1Id, 1),
            response = MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE)
        )
        mockWebServer.dispatcher = RequestBasedDispatcher
        mockWebServer.start()
        val musicRepository = createMusicRepository(mockWebServer)

        val albumsRequest = musicRepository.getAlbums(band1)

        assertTrue(albumsRequest.isFailure)
        assertTrue(albumsRequest.exceptionOrNull() is SocketTimeoutException)
    }

    @Test
    fun getBandsShallReturnFailureForSocketTimeoutAndNotCrashApp() = runTest(testDispatcher){
        RequestBasedDispatcher.setResponseForRequest(
            path = getPathForBandSearch("blah", 1),
            response = MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE)
        )
        mockWebServer.dispatcher = RequestBasedDispatcher
        mockWebServer.start()
        val musicRepository = createMusicRepository(mockWebServer)

        val bandsRequest = musicRepository.getBands("blah", 1)

        assertTrue(bandsRequest.isFailure)
        assertTrue(bandsRequest.exceptionOrNull() is SocketTimeoutException)
    }

    @Test
    fun getBandShallReturnFailureForSocketTimeoutAndNotCrashApp() = runTest(testDispatcher){
        RequestBasedDispatcher.setResponseForRequest(
            path = getPathForMetadataRequest("Blah"),
            response = MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE)
        )
        mockWebServer.dispatcher = RequestBasedDispatcher
        mockWebServer.start()
        val musicRepository = createMusicRepository(mockWebServer)

        val bandRequest = musicRepository.getBand("Blah")

        assertTrue(bandRequest.isFailure)
        testLogger.d("temp", "ex: ${bandRequest.exceptionOrNull()}")
        assertTrue(bandRequest.exceptionOrNull() is SocketTimeoutException)
    }

}