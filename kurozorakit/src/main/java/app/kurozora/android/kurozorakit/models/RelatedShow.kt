package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// A root object that stores information about a related show resource.
@Serializable
data class RelatedShow @OptIn(ExperimentalUuidApi::class) constructor(
    // The id of the related show.
    val id: Uuid = Uuid.random(),
    // The show related to the parent show.
    val show: Show,
    // The attributes belonging to the related show.
    var attributes: Attributes,
) {
    // MARK: - Functions
    @OptIn(ExperimentalUuidApi::class)
    override fun equals(other: Any?): Boolean {
        return (other is RelatedShow) && this.id == other.id
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun hashCode(): Int {
        return id.hashCode()
    }

    // MARK: - Attributes
    @Serializable
    data class Attributes(
        /// The relation between the show.
        val relation: MediaRelation,
    )
}
