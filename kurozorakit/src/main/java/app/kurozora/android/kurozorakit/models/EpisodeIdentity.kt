package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class EpisodeIdentity(
    val id: String,
    val type: String = "episodes",
    val href: String = "",
) {
    override fun equals(other: Any?): Boolean {
        return (other is EpisodeIdentity) && this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
