package app.kurozora.android.kurozorakit.enums

enum class MediaCollection {
    artwork,
    banner,
    logo,
    poster,
    profile,
    screenshot,
    symbol;

    val stringValue: String
        get() = when (this) {
            artwork -> "Artwork"
            banner -> "Banner"
            logo -> "Logo"
            poster -> "Poster"
            profile -> "Profile"
            screenshot -> "Screenshot"
            symbol -> "Symbol"
        }
}
