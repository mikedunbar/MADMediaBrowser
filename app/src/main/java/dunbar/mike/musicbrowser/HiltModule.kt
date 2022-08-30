package dunbar.mike.musicbrowser

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dunbar.mike.musicbrowser.api.ArchiveApi
import dunbar.mike.musicbrowser.api.ArchiveMusicApiAdapter
import dunbar.mike.musicbrowser.model.FakeMusicRepo
import dunbar.mike.musicbrowser.model.MusicRepo
import dunbar.mike.musicbrowser.model.RealMusicRepo
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {

    @Provides
    fun provideMusicRepo(archiveApi: ArchiveApi): MusicRepo {
        val forReal = false
        return if (forReal) RealMusicRepo(ArchiveMusicApiAdapter(archiveApi)) else FakeMusicRepo()
    }

    @Provides
    fun provideArchiveApi(): ArchiveApi {
        val okHttpClient = OkHttpClient.Builder()
            .apply {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(loggingInterceptor)
            }
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://archive.org/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ArchiveApi::class.java)
    }

}