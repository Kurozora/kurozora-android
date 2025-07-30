package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class SeasonIdentity(
    val id: String,
    val type: String = "seasons",
    val href: String = "",
) {
    override fun equals(other: Any?): Boolean {
        return (other is SeasonIdentity) && this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
