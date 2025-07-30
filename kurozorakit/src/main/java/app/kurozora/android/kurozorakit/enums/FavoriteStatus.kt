package app.kurozora.android.kurozorakit.enums

import kotlinx.serialization.Serializable

@Serializable
enum class FavoriteStatus(val value: Int) {
    notFavorited(-1),
    disabled(0),
    favorited(1);

    companion object {
        fun fromBoolean(bool: Boolean?): FavoriteStatus {
            return when (bool) {
                true -> favorited
                false -> notFavorited
                null -> disabled
            }
        }
    }
}
