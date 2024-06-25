@file:Suppress("PropertyName") // Using api names, for clarity

package dunbar.mike.mediabrowser.data.music.archiveapi

import com.squareup.moshi.JsonClass

//region Band Search
@JsonClass(generateAdapter = true)
data class BandSearchResponse(
    val response: BandSearchResponsePayload
)

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
)
//endregion

//region Album Search
@JsonClass(generateAdapter = true)
data class AlbumSearchResponse(
    val response: AlbumSearchResponsePayload
)

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
)

//endregion

//region Item Metadata
@JsonClass(generateAdapter = true)
data class MetadataResponse(
    val server: String,
    val dir: String,
    val subject: String?,
    val files: List<MetadataFile>,
    val metadata: Metadata
)

@JsonClass(generateAdapter = true)
data class Metadata(
    val creator: String,
    val title: String?,
    val date: String?,
)

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

