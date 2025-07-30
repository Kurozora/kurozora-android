package app.kurozora.android.kurozorakit.enums

enum class MediaKind {
    episodes,
    games,
    literatures,
    shows;

    val stringValue: String
        get() = when (this) {
            episodes -> "Episodes"
            games -> "Games"
            literatures -> "Literatures"
            shows -> "Shows"
        }
}
