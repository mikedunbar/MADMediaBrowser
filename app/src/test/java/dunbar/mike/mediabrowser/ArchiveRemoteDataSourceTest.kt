package dunbar.mike.mediabrowser

import dunbar.mike.mediabrowser.data.music.archiveapi.ArchiveApi
import dunbar.mike.mediabrowser.data.music.archiveapi.ArchiveRemoteDataSource
import dunbar.mike.mediabrowser.util.ConsoleLogger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.String.format
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class ArchiveRemoteDataSourceTest {
    private val logger = ConsoleLogger()
    private val archiveApi = createArchiveApi()
    private val archiveDataSource = ArchiveRemoteDataSource(archiveApi, logger)

    // Bogus test...to get API working
    @Test
    fun `test searchDeadShows`() = runBlocking {
        val albums = archiveDataSource.getAlbums("Grateful Dead").getOrThrow()
        print(format("In test, got albums %s", albums))
        assertEquals(10, albums.size)
    }

    @Test
    fun `test searchBands`() = runBlocking {
        val bands = archiveDataSource.getBands()
        print(format("In test, got bands %s", bands))
    }

    private fun createArchiveApi(): ArchiveApi {
        val okHttpClient = OkHttpClient.Builder()
            .apply {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(loggingInterceptor)
            }
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        val archiveApi = Retrofit.Builder()
            .baseUrl("https://archive.org/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ArchiveApi::class.java)
        return archiveApi
    }
}