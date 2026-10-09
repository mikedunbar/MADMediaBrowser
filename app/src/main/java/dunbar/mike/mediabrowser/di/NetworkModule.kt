package dunbar.mike.mediabrowser.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dunbar.mike.mediabrowser.data.music.MusicRemoteDataSource
import dunbar.mike.mediabrowser.data.music.archiveapi.ArchiveApi
import dunbar.mike.mediabrowser.data.music.archiveapi.ArchiveRemoteDataSource
import dunbar.mike.mediabrowser.util.Logger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Module
    @InstallIn(SingletonComponent::class)
    object NetworkModule {

        @Provides
        fun provideOkHttpClient(logger: Logger): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val dispatcher = okhttp3.Dispatcher().apply {
                maxRequests = 64
                maxRequestsPerHost = 20
            }

            val timingInterceptor = okhttp3.Interceptor { chain ->
                val request = chain.request()
                val startNs = System.nanoTime()

                val response = chain.proceed(request) // Executes the network call

                val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
                logger.d("NetworkTiming", "${request.method} ${request.url.encodedPath} took ${tookMs}ms [Code: ${response.code}]")

                response
            }

            return OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .dispatcher(dispatcher)
                .addInterceptor(timingInterceptor)      // Add timing interceptor
                .addInterceptor(loggingInterceptor)   // Add body logging interceptor
                .build()
        }
    }

    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    fun provideArchiveApi(okHttpClient: OkHttpClient, moshi: Moshi): ArchiveApi = Retrofit.Builder()
        .baseUrl("https://archive.org/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okHttpClient)
        .build()
        .create(ArchiveApi::class.java)

    @Provides
    fun provideMusicRemoteDataSource(api: ArchiveApi, logger: Logger): MusicRemoteDataSource {
        return ArchiveRemoteDataSource(api, logger)
    }

}