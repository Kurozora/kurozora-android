package app.kurozora.android.kurozorakit.models

import kotlinx.serialization.Serializable

@Serializable
data class Country(
    val name: String,
    val code: String,
    val iso31663: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Country) return false
        return code == other.code
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}

