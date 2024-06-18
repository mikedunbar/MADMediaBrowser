package dunbar.mike.mediabrowser.data.music.archiveapi

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import dunbar.mike.mediabrowser.data.music.Album
import dunbar.mike.mediabrowser.data.music.Band
import dunbar.mike.mediabrowser.data.music.Song
import dunbar.mike.mediabrowser.util.localDateTimeFromIsoInstant

sealed interface SearchResponse

//region Search  Bands
@JsonClass(generateAdapter = true)
data class BandSearchResponse(
    @Json(name = "response")
    val bandSearchResponsePayload: BandSearchResponsePayload
) : SearchResponse

@JsonClass(generateAdapter = true)
data class BandSearchResponsePayload(
    val numFound: Int,
    val start: Int,
    val docs: List<BandSearchResponseDoc>
)

@JsonClass(generateAdapter = true)
data class BandSearchResponseDoc(
    val creator: String,
    val identifier: String,
    val publicdate: String?,
)


//endregion

//region Search Shows
@JsonClass(generateAdapter = true)
data class AlbumSearchSuccessResponse(
    @Json(name = "response")
    val albumSearchResponsePayload: AlbumSearchResponsePayload
) : SearchResponse

@JsonClass(generateAdapter = true)
data class AlbumSearchResponsePayload(
    val numFound: Int,
    val start: Int,
    val docs: List<AlbumSearchResponseDoc>
)

@JsonClass(generateAdapter = true)
data class AlbumSearchResponseDoc(
    val creator: String,
    val title: String,
    val date: String,
    val avg_rating: Double?,
    val identifier: String,
    val downloads: Int,
)

data class ArchiveAlbum(
    val responseDoc: AlbumSearchResponseDoc,
    val metadataResponse: MetadataResponse,
) {
    fun asAlbum(): Album {
        val songList = when (metadataResponse) {
            is SuccessfulMetadataResponse -> {
                val fileSize = metadataResponse.files.size
                List(fileSize) {
                    Song(
                        metadataResponse.files[it].title
                            ?: "unknown",
                        metadataResponse.files[it].length?.toDoubleOrNull()
                    )
                }
            }
        }

        return Album(
            band = Band(
                name = responseDoc.creator,
                description = "Rock",
                id = responseDoc.identifier,
            ),
            name = responseDoc.title,
            id = responseDoc.identifier,
            releaseDate = localDateTimeFromIsoInstant(responseDoc.date).toLocalDate(),
            songs = songList,
        )
    }
}

//endregion

//region Metadata

sealed interface MetadataResponse

@JsonClass(generateAdapter = true)
data class SuccessfulMetadataResponse(
    val server: String,
    val dir: String,
    val subject: String?,
    val files: List<MetadataFile>
) : MetadataResponse

@JsonClass(generateAdapter = true)
data class MetadataFile(
    val name: String,
    val source: String,
    val format: String,
    val length: String?,
    val title: String?
)

enum class SupportedAudioFile(val ids: List<String>) {
    FLAC(listOf("Flac", "24bit Flac")),

    ;

}

