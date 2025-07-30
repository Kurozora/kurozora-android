package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// A root object that stores information about a related game resource.
@Serializable
data class RelatedGame @OptIn(ExperimentalUuidApi::class) constructor(
    // The id of the related game.
    val id: Uuid = Uuid.random(),
    // The game related to the parent game.
    val game: Game,
    // The attributes belonging to the related game.
    var attributes: Attributes,
) {
    // MARK: - Functions
    @OptIn(ExperimentalUuidApi::class)
    override fun equals(other: Any?): Boolean {
        return (other is RelatedGame) && this.id == other.id
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun hashCode(): Int {
        return id.hashCode()
    }

    // MARK: - Attributes
    @Serializable
    data class Attributes(
        /// The relation between the game.
        val relation: MediaRelation,
    )
}
