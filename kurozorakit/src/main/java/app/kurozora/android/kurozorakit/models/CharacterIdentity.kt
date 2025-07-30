package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class CharacterIdentity(
    val id: String,
    val type: String = "characters",
    val href: String = "",
) {
    override fun equals(other: Any?): Boolean {
        return (other is CharacterIdentity) && this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
