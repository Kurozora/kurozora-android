package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class Media(
    val url: String,
    val height: Int? = null,
    val width: Int? = null,
    val backgroundColor: String? = null,
    val textColor1: String? = null,
    val textColor2: String? = null,
    val textColor3: String? = null,
    val textColor4: String? = null,
    val relationships: Relationships? = null,
) {
    @Serializable
    data class Relationships(
        val episodes: EpisodeIdentityResponse? = null,
        val games: GameIdentityResponse? = null,
        val literatures: LiteratureIdentityResponse? = null,
        val shows: ShowIdentityResponse? = null,
    )
}
