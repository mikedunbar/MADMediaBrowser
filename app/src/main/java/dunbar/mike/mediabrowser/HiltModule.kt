package dunbar.mike.mediabrowser

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dunbar.mike.mediabrowser.data.music.MusicRemoteDataSource
import dunbar.mike.mediabrowser.data.music.MusicRepo
import dunbar.mike.mediabrowser.data.music.archiveapi.ArchiveApi
import dunbar.mike.mediabrowser.data.music.archiveapi.ArchiveApiMusicRemoteDataSource
import dunbar.mike.mediabrowser.data.user.RealUserDataSource
import dunbar.mike.mediabrowser.data.user.UserDataRepo
import dunbar.mike.mediabrowser.data.user.UserDataSource
import dunbar.mike.mediabrowser.util.AndroidLogger
import dunbar.mike.mediabrowser.util.Logger
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
    fun provideMusicRemoteDataSource(api: ArchiveApi, logger: Logger): MusicRemoteDataSource {
//        return FakeMusicRemoteDataSource()
        return ArchiveApiMusicRemoteDataSource(api, logger)
    }

    @Provides
    fun provideLogger(): Logger = AndroidLogger

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

    @Provides
    fun provideUserDataRepo(userDataSource: UserDataSource): UserDataRepo = UserDataRepo(userDataSource)

    @Provides
    fun provideUserDataDataSource(@ApplicationContext context: Context): UserDataSource = RealUserDataSource(context)

}