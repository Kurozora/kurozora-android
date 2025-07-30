package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    override val id: String,
    override val type: String,
    override val href: String,
    val attributes: Attributes,
    val relationships: Relationships? = null,
) : IdentityResource {
    @Serializable
    data class Attributes(
        val amazonID: String? = null,
        val amID: Long? = null,
        val deezerID: Long? = null,
        val malID: Int? = null,
        val spotifyID: String? = null,
        val youtubeID: String? = null,
        val artwork: Media? = null,
        val originalTitle: String? = null,
        val title: String,
        val originalLyrics: String? = null,
        val lyrics: String? = null,
        val artist: String,
        val stats: MediaStat? = null,
        val copyright: String? = null,
        var library: LibraryAttributes? = null,
    )

    @Serializable
    data class Relationships(
        val shows: ShowIdentityResponse? = null,
        val games: GameIdentityResponse? = null,
    )
}
