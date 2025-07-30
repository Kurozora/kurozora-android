package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class GameIdentity(
    val id: String,
    val type: String = "games",
    val href: String = "",
) {
    override fun equals(other: Any?): Boolean {
        return (other is GameIdentity) && this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
