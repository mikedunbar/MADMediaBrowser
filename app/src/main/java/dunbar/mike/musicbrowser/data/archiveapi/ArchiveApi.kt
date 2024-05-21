package dunbar.mike.musicbrowser.data.archiveapi

import retrofit2.http.GET
import retrofit2.http.Path


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

    @GET(
        "advancedsearch.php?output=json&rows=10" +
                "&fl[]=date&fl[]=title&fl[]=avg_rating" +
                "&fl[]=identifier&fl[]=downloads&fl[]=creator" +
                "&q=collection:(GratefulDead)"
    )
    suspend fun searchDeadShows(): SuccessSearchResponse

    @GET("metadata/{archiveId}")
    suspend fun getMetaData(@Path("archiveId") archiveId: String): SuccessfulMetadataResponse

}