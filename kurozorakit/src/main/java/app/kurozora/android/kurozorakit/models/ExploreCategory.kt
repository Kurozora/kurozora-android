package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

/// A root object that stores information about an explore category resource.
@Serializable
data class ExploreCategory(
    override val id: String,
    override val type: String,
    override val href: String,
    val attributes: Attributes,
    val relationships: Relationships? = null,
) : IdentityResource {
    @Serializable
    data class Attributes(
        // MARK: - Properties
        /// The slug of the resource.
        val slug: String,
        /// The title of the explore category.
        val title: String,
        /// The description of the explore category.
        val description: String?,
        /// The position of the explore category.
        val position: Int,
        /// The type of the explore category.
        val type: String,
        /// The size of the explore category.
        val size: String,
    )

    @Serializable
    data class Relationships(
        val characters: CharacterIdentityResponse? = null,
        val episodes: EpisodeIdentityResponse? = null,
        val games: GameIdentityResponse? = null,
        val literatures: LiteratureIdentityResponse? = null,
        val people: PersonIdentityResponse? = null,
        val showSongs: ShowSongResponse? = null,
//        val genres: GenreIdentityResource? = null,
//        val themes: ThemeIdentityResource? = null,
        val shows: ShowIdentityResponse? = null,
//        val recaps: RecapResource? = null
    )
}
