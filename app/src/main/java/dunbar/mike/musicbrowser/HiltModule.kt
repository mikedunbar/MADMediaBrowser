package dunbar.mike.musicbrowser

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dunbar.mike.musicbrowser.data.FakeMusicRemoteDataSource
import dunbar.mike.musicbrowser.data.MusicRemoteDataSource
import dunbar.mike.musicbrowser.data.MusicRepo
import dunbar.mike.musicbrowser.data.archiveapi.ArchiveApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {

    @Provides
    fun provideMusicRepo(
        remoteDataSource: MusicRemoteDataSource,
        ioDispatcher: CoroutineDispatcher,
    ): MusicRepo {
        return MusicRepo(remoteDataSource, ioDispatcher)
    }

    @Provides
    fun provideMusicRemoteDataSource(): MusicRemoteDataSource = FakeMusicRemoteDataSource()

    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

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