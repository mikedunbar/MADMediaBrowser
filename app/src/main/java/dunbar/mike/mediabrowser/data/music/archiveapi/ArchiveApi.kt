package dunbar.mike.mediabrowser.data.music.archiveapi

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


/**
 * An item’s “details” page will always be available at
http://archive.org/details/[identifier]
http://archive.org/details/gd1984-04-29.149679.beyerm201.holbrook.flac2448

The item directory is always available at
http://archive.org/download/[identifier]
https://archive.org/download/gd1984-04-29.149679.beyerm201.holbrook.flac2448

A particular file can always be downloaded from
http://archive.org/download/[identifier]/[filename]
https://archive.org/download/gd1984-04-29.149679.beyerm201.holbrook.flac2448/gd1984-04-29beyerm201s1t01.flac

An item’s metadata may be fetched by making an HTTP GET request to
https://archive.org/metadata/{identifier}
https://archive.org/metadata/gd1984-04-29.149679.beyerm201.holbrook.flac2448

 */
@Suppress("KDocUnresolvedReference")
interface ArchiveApi {

    @GET("advancedsearch.php?output=json&fl[]=date,title,avg_rating,identifier,downloads,creator")
    suspend fun searchAlbums(@Query("rows") rows: Int, @Query("page") page: Int, @Query("q")query: String): AlbumSearchSuccessResponse

    @GET("advancedsearch.php?output=json&fl[]=creator,identifier,publicdate&sort[]=creator asc&q=collection:etree AND mediatype:collection")
    suspend fun searchBands(@Query("rows") rows: Int, @Query("page") page: Int): Response<BandSearchResponse>

    @GET("metadata/{archiveId}")
    suspend fun getMetaData(@Path("archiveId") archiveId: String): SuccessfulMetadataResponse

    class Exception(message: String, cause: Throwable? = null) : kotlin.Exception(message, cause)

}