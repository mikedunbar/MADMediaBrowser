package dunbar.mike.mediabrowser.data.music

import dunbar.mike.mediabrowser.data.music.TestFileReader.fileContentsAsString
import dunbar.mike.mediabrowser.data.music.archiveapi.ArchiveApi
import dunbar.mike.mediabrowser.data.music.archiveapi.ArchiveRemoteDataSource
import dunbar.mike.mediabrowser.util.ConsoleLogger
import kotlinx.coroutines.test.StandardTestDispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.URLEncoder
import java.time.LocalDate
import java.util.concurrent.TimeUnit

val band1Id = "EastNashGrass"
val band1Name = "East Nash Grass"
val band1Description = "East Nash Grass"
val band1 = Band(band1Name, band1Description, band1Id)

val band1Album1 = Album(
    band = band1,
    name = "East Nash Grass Live at Moravian Pottery and Tileworks on 2022-07-31",
    id = "east-nash-grass",
    releaseDate = LocalDate.of(2022, 7, 31),
    songs = listOf(
        Song(name = "20220731 EastNashGrass-Tileworks(mk4v414o)/01. Intro.flac", durationSeconds = 37.17),
        Song(name = "20220731 EastNashGrass-Tileworks(mk4v414o)/02. Instrumental .flac", durationSeconds = 161.91),
        Song(name = "20220731 EastNashGrass-Tileworks(mk4v414o)/03. Country Blue .flac", durationSeconds = 162.49),
        Song(name = "20220731 EastNashGrass-Tileworks(mk4v414o)/04. Banter.flac", durationSeconds = 76.37),
        Song(name = "20220731 EastNashGrass-Tileworks(mk4v414o)/05. Mother Was Sleeping .flac", durationSeconds = 304.03),
        Song(name = "20220731 EastNashGrass-Tileworks(mk4v414o)/06. My Window Faces the South.flac", durationSeconds = 268.27),
    )
)

val band1Album2 = Album(
    band = band1,
    name = "East Nash Grass Live at Wild Horse Saloon on 2024-04-21",
    id = "EastNashGrass2024-04-21.flac16-schoepsmk4-soundboardmatrix",
    releaseDate = LocalDate.of(2024, 4, 21),
    songs = listOf(
        Song(name = "eastnashgrass2024-04-21-t-01.flac", durationSeconds = 28.35),
        Song(name = "eastnashgrass2024-04-21-t-02.flac", durationSeconds = 150.59),
        Song(name = "eastnashgrass2024-04-21-t-03.flac", durationSeconds = 431.97),
        Song(name = "eastnashgrass2024-04-21-t-04.flac", durationSeconds = 241.29),
        Song(name = "eastnashgrass2024-04-21-t-05.flac", durationSeconds = 103.19),
        Song(name = "eastnashgrass2024-04-21-t-06.flac", durationSeconds = 170.07),
    )
)

val band1Album3 = Album(
    band = band1,
    name = "East Nash Grass Live at 3rd & Lindsley Nashville, TN on 2023-12-28",
    id = "EastNashGrass2023-12-28",
    releaseDate = LocalDate.of(2023, 12, 28),
    songs = listOf(
        Song(name = "EastNashGrass_2023-12-28.24-48.t01.flac", durationSeconds = 185.12),
        Song(name = "EastNashGrass_2023-12-28.24-48.t02.flac", durationSeconds = 230.56),
        Song(name = "EastNashGrass_2023-12-28.24-48.t03.flac", durationSeconds = 305.12),
        Song(name = "EastNashGrass_2023-12-28.24-48.t04.flac", durationSeconds = 154.12),
        Song(name = "EastNashGrass_2023-12-28.24-48.t05.flac", durationSeconds = 355.19),
        Song(name = "EastNashGrass_2023-12-28.24-48.t06.flac", durationSeconds = 289.95),
    )
)

val band2Id = "EastCoastDaveAndTheMidwestSwingers"
val band2Name = "East Coast Dave and the Midwest Swingers"
val band2Description = "East Coast Dave and the Midwest Swingers"
val band2 = Band(band2Name, band2Description, band2Id)

val band3Id = "EastCoastDave"
val band3Name = "East Coast Dave"
val band3Description = "East Coast Dave"
val band3 = Band(band3Name, band3Description, band3Id)

val band1Band2Band3BandSearchResponse = fileContentsAsString("Band1Band2Band3SearchResponse.json")
val band1MetadataResponse = fileContentsAsString("Band1MetadataResponse.json")
val band2MetadataResponse = fileContentsAsString("Band2MetadataResponse.json")
val band3MetadataResponse = fileContentsAsString("Band3MetadataResponse.json")
val band1AlbumSearchResponse = fileContentsAsString("Band1AlbumSearchResponse.json")
val band1Album1MetadataResponse = fileContentsAsString("Band1Album1MetadataResponse.json")
val band1Album2MetadataResponse = fileContentsAsString("Band1Album2MetadataResponse.json")
val band1Album3MetadataResponse = fileContentsAsString("Band1Album3MetadataResponse.json")

val testDispatcher = StandardTestDispatcher()
val testLogger = ConsoleLogger()

fun createMusicRepository(
    mockServer: MockWebServer
): MusicRepository {
    return MusicRepository(
        ArchiveRemoteDataSource(
            createTestApi(mockServer),
            testLogger,
            testDispatcher
        ),
        testDispatcher
    )
}

fun createTestApi(mockServer: MockWebServer): ArchiveApi {
    val okHttpClient = OkHttpClient.Builder()
        .apply {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            addInterceptor(loggingInterceptor)
        }
        // Timeouts low to speed up tests, since we are using a mock server anyhow and real responses are instantaneous
        .connectTimeout(1, TimeUnit.SECONDS)
        .readTimeout(1, TimeUnit.SECONDS)
        .writeTimeout(1, TimeUnit.SECONDS)
        .build()

    return Retrofit.Builder()
        .baseUrl(mockServer.url("/"))
        .addConverterFactory(MoshiConverterFactory.create())
        .client(okHttpClient)
        .build()
        .create(ArchiveApi::class.java)
}

// Cannot get the class/resource loader working in a top-level function
object TestFileReader {
    fun fileContentsAsString(fileName: String) =
        javaClass.classLoader?.getResourceAsStream(fileName)!!.bufferedReader().use { it.readText() }
}

fun getPathForBandSearch(searchString: String, resultsPage: Int = 1): String {
    val encodedQueryString = URLEncoder.encode("collection:etree AND mediatype:collection AND creator:${searchString}*", "UTF-8")
        .replace("+", "%20") // for some reason URLEncoder encodes spaces as "+", whereas Retrofit is encoding them as "%20"
    return "/advancedsearch.php?output=json&fl[]=creator,identifier,publicdate&sort[]=creator%20asc&rows=20&page=$resultsPage&q=$encodedQueryString"
}

fun getPathForAlbumSearch(bandId: String, resultsPage: Int = 1): String {
    return "/advancedsearch.php?output=json&fl[]=date,title,avg_rating,identifier,downloads,creator&rows=20&page=$resultsPage&q=collection%3A%28$bandId%29"
}

fun getPathForMetadataRequest(itemId: String): String {
    return "/metadata/$itemId"
}

object RequestBasedDispatcher : Dispatcher() {
    private val map = mutableMapOf<String, MockResponse>()

    fun setResponseForRequest(path: String, response: MockResponse) {
        map[path] = response
    }

    override fun dispatch(request: RecordedRequest): MockResponse {
        return map[request.path] ?: MockResponse().setResponseCode(404)
    }

}

