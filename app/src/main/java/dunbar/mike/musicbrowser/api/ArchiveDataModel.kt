package dunbar.mike.musicbrowser.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import dunbar.mike.musicbrowser.model.Album
import dunbar.mike.musicbrowser.model.Band
import dunbar.mike.musicbrowser.model.Song
import dunbar.mike.musicbrowser.util.localDateFromIsoInstant

sealed interface SearchResponse

@JsonClass(generateAdapter = true)
data class SuccessSearchResponse(
    @Json(name = "response")
    val searchResponsePayload: SearchResponsePayload
) : SearchResponse

@JsonClass(generateAdapter = true)
data class SearchResponsePayload(
    val numFound: Int,
    val start: Int,
    val docs: List<SearchResponseDoc>
)

@JsonClass(generateAdapter = true)
data class SearchResponseDoc(
    val creator: String,
    val title: String,
    val date: String,
    val avg_rating: Double?,
    val identifier: String,
    val downloads: Int,
)

data class Show(
    val responseDoc: SearchResponseDoc,
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
                responseDoc.creator,
                "Rock",
            ),
            responseDoc.title,
            localDateFromIsoInstant(responseDoc.date),
            songList,
        )
    }
}

sealed interface MetadataResponse

class ErrorMetadataResponse(val error: Throwable) : MetadataResponse

@JsonClass(generateAdapter = true)
class SuccessfulMetadataResponse(
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

