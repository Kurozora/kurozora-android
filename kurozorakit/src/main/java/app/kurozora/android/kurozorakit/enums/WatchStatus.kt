package app.kurozora.android.kurozorakit.enums

enum class WatchStatus(val value: Int) {
    notWatched(-1),
    disabled(0),
    watched(1);

    val stringValue: String
        get() = when (this) {
            notWatched -> "Not Watched"
            disabled -> "Disabled"
            watched -> "Watched"
        }

    companion object {
        fun fromBoolean(bool: Boolean?): WatchStatus {
            return when (bool) {
                true -> watched
                false -> notWatched
                null -> disabled
            }
        }
    }
}
