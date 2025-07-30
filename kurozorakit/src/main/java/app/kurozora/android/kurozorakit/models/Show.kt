package app.kurozora.android.kurozorakit.models

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonNames

internal interface IdentityResource {
    val id: String
    val type: String
    val href: String
}

@Serializable
data class ShowAttributes(
    @JsonNames("startedAt") val startedAtTimestamp: Long? = null,
    @JsonNames("endedAt") val endedAtTimestamp: Long? = null,
    @JsonNames("nextBroadcastAt") val nextBroadcastAtTimestamp: Long? = null,
) {
    @Transient
    val startedAt: Instant? = startedAtTimestamp?.let { Instant.fromEpochSeconds(it) }

    @Transient
    val endedAt: Instant? = endedAtTimestamp?.let { Instant.fromEpochSeconds(it) }

    @Transient
    val nextBroadcastAt: Instant? = nextBroadcastAtTimestamp?.let { Instant.fromEpochSeconds(it) }
}

// Custom Serializer for LocalDate from/to Timestamp (Long)
object LocalDateSerializer : KSerializer<LocalDate?> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: LocalDate?) {
        if (value == null) {
            encoder.encodeNull()
        } else {
            // Convert LocalDate to epoch days and then to milliseconds
            // Assuming the timestamp is in milliseconds since epoch
            // Adjust if your timestamp is in seconds
            val instant = value.atStartOfDayIn(TimeZone.UTC)
            encoder.encodeLong(instant.toEpochMilliseconds())
        }
    }

    override fun deserialize(decoder: Decoder): LocalDate? {
        if (decoder.decodeNotNullMark()) {
            val timestamp = decoder.decodeLong()
            // Convert milliseconds since epoch to Instant and then to LocalDate
            // Adjust if your timestamp is in seconds
            return Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(TimeZone.UTC).date
        }
        return null
    }
}

@Serializable
data class Show(
    override val id: String,
    override val type: String,
    override val href: String,
    val attributes: Attributes,
    val relationships: Relationships? = null,
) : IdentityResource {
    @Serializable
    data class Attributes(
        val anidbID: Int? = null,
        val anilistID: Int? = null,
        val animePlanetID: String? = null,
        val anisearchID: Int? = null,
        val imdbID: String? = null,
        val kitsuID: Int? = null,
        val malID: Int? = null,
        val notifyID: String? = null,
        val syoboiID: Int? = null,
        val traktID: Int? = null,
        val tvdbID: Int? = null,
        val slug: String,
        val videoUrl: String? = null,
        val poster: Media? = null,
        val banner: Media? = null,
        val logo: Media? = null,
        val originalTitle: String? = null,
        val title: String,
        val synonymTitles: List<String>? = null,
        val tagline: String? = null,
        val synopsis: String? = null,
        val genres: List<String>? = null,
        val themes: List<String>? = null,
        val studio: String? = null,
        val languages: List<Language>,
        val countryOfOrigin: Country? = null,
        val tvRating: TVRating,
        val type: MediaType,
        val source: AdaptationSource,
        val status: AiringStatus,
        val episodeCount: Int,
        val seasonCount: Int,
        val stats: MediaStat? = null,
        @Serializable(with = LocalDateSerializer::class)
        val startedAt: LocalDate? = null,
        @Serializable(with = LocalDateSerializer::class)
        val endedAt: LocalDate? = null,
        @Serializable(with = LocalDateSerializer::class)
        val nextBroadcastAt: LocalDate? = null,
        val duration: String,
        val durationCount: Int,
        val durationTotal: String,
        val durationTotalCount: Int,
        val airSeason: String? = null,
        val airTime: String? = null,
        val airDay: String? = null,
        val isNSFW: Boolean,
        val copyright: String? = null,
        var library: LibraryAttributes? = null,
    )

    @Serializable
    data class Relationships(
        val cast: CastIdentityResponse? = null,
        val characters: CharacterIdentityResponse? = null,
        val people: PersonIdentityResponse? = null,
        val relatedShows: RelatedShowResponse? = null,
        val relatedGames: RelatedGameResponse? = null,
        val relatedLiteratures: RelatedLiteratureResponse? = null,
        val seasons: SeasonIdentityResponse? = null,
        val showSongs: ShowSongResponse? = null,
        val songs: SongIdentityResponse? = null,
        val staff: StaffIdentityResponse? = null,
        val studios: StudioIdentityResponse? = null,
    )
}
