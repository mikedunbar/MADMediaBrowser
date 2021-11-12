package dunbar.mike.musicbrowser

import dunbar.mike.musicbrowser.api.ArchiveApi
import dunbar.mike.musicbrowser.api.ArchiveMusicApiAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.String.format
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class ArchiveMusicApiAdapterTest {

    private val archiveApi = createArchiveApi()
    private val objectUnderTest = ArchiveMusicApiAdapter(archiveApi)

    @Test
    fun `test searchDeadShows`() = runBlocking {
        val albums = objectUnderTest.getAlbums("Grateful Dead")
        print(format("In test, got albums %s", albums))
        assertEquals(10, albums.size)
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