package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class MediaType(
    val name: String,
    val description: String,
) {
    override fun equals(other: Any?): Boolean {
        return (other is MediaType) && name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
