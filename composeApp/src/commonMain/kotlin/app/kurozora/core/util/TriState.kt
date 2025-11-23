package app.kurozora.core.util

enum class TriState {
    NONE, INCLUDE, EXCLUDE;

    fun next(): TriState = when (this) {
        NONE -> INCLUDE
        INCLUDE -> EXCLUDE
        EXCLUDE -> NONE
    }
}
