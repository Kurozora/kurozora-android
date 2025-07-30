package app.kurozora.android.kurozorakit.enums

import kotlinx.serialization.Serializable

@Serializable
enum class FollowStatus(val value: Int) {
    notFollowed(-1),
    disabled(0),
    followed(1);

    companion object {
        fun fromBoolean(bool: Boolean?): FollowStatus {
            return when (bool) {
                true -> followed
                false -> notFollowed
                null -> disabled
            }
        }
    }
}
