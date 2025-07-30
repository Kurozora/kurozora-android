package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class AiringStatus(
    val name: String,
    val description: String,
    val color: String,
) {
    override fun equals(other: Any?): Boolean {
        return (other is AiringStatus) && name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
