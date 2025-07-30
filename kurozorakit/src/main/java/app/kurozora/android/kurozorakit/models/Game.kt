package app.kurozora.android.kurozorakit.models

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonNames

@Serializable
data class GameAttributes(
    @JsonNames("startedAt") val startedAtTimestamp: Long? = null,
    @JsonNames("endedAt") val endedAtTimestamp: Long? = null,
    @JsonNames("nextPublicationAt") val nextPublicationAtTimestamp: Long? = null,
) {
    @Transient
    val startedAt: Instant? = startedAtTimestamp?.let { Instant.fromEpochSeconds(it) }

    @Transient
    val endedAt: Instant? = endedAtTimestamp?.let { Instant.fromEpochSeconds(it) }

    @Transient
    val nextPublicationAt: Instant? = nextPublicationAtTimestamp?.let { Instant.fromEpochSeconds(it) }
}

@Serializable
data class Game(
    override val id: String,
    override val type: String,
    override val href: String,
    val attributes: Attributes,
    val relationships: Relationships? = null,
) : IdentityResource {
    @Serializable
    data class Attributes(
        val igdb: Int? = null,
        val igdbSlug: String? = null,
        val slug: String,
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
        val editionCount: Int,
        val stats: MediaStat? = null,
        val startedAt: LocalDate? = null,
        val endedAt: LocalDate? = null,
        val duration: String,
        val durationCount: Int,
        val durationTotal: String,
        val durationTotalCount: Int,
        val publicationSeason: String? = null,
        val publicationTime: String? = null,
        val publicationDay: String? = null,
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
        val songs: SongIdentityResponse? = null,
        val staff: StaffIdentityResponse? = null,
        val studios: StudioIdentityResponse? = null,
    )
}
