package app.kurozora.android.kurozorakit.enums

enum class KKSearchScope(val value: Int) {
    kurozora(0),
    library(1);

    val queryValue: String
        get() = when (this) {
            kurozora -> "kurozora"
            library -> "library"
        }
}
