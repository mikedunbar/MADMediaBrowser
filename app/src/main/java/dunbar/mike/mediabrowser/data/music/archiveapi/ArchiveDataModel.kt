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
data class BandSearchSuccessResponse(
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
    val publicdate: String,
)


//endregion

//region Search Shows
@JsonClass(generateAdapter = true)
data class ShowSearchSuccessResponse(
    @Json(name = "response")
    val showSearchResponsePayload: ShowSearchResponsePayload
) : SearchResponse

@JsonClass(generateAdapter = true)
data class ShowSearchResponsePayload(
    val numFound: Int,
    val start: Int,
    val docs: List<ShowSearchResponseDoc>
)

@JsonClass(generateAdapter = true)
data class ShowSearchResponseDoc(
    val creator: String,
    val title: String,
    val date: String,
    val avg_rating: Double?,
    val identifier: String,
    val downloads: Int,
)

data class Show(
    val responseDoc: ShowSearchResponseDoc,
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

            is ErrorMetadataResponse -> {
                listOf()
            }
        }

        return Album(
            Band(
                name = responseDoc.creator,
                genre = "Rock",
                id = responseDoc.identifier,
            ),
            responseDoc.title,
            localDateTimeFromIsoInstant(responseDoc.date).toLocalDate(),
            songList,
        )
    }
}

//endregion

//region Metadata

sealed interface MetadataResponse

class ErrorMetadataResponse(val error: Throwable) : MetadataResponse

@JsonClass(generateAdapter = true)
data class SuccessfulMetadataResponse(
    val dir: String,
    val files: List<MetadataFile>
) : MetadataResponse

@JsonClass(generateAdapter = true)
data class MetadataFile(
    val name: String,
    val format: String,
    val length: String?,
    val title: String?
)

enum class SupportedAudioFile(val ids: List<String>) {
    FLAC(listOf("Flac", "24bit Flac")),

    ;

}

