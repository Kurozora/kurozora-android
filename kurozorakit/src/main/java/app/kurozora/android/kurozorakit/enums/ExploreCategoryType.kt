package app.kurozora.android.kurozorakit.enums

import kotlinx.serialization.Serializable

@Serializable
enum class ExploreCategoryType(val value: String) {
    mostPopularShows("most-popular-shows"),
    upcomingShows("upcoming-shows"),
    newShows("new-shows"),
    shows("shows"),
    mostPopularLiteratures("most-popular-literatures"),
    upcomingLiteratures("upcoming-literatures"),
    newLiteratures("new-literatures"),
    literatures("literatures"),
    mostPopularGames("most-popular-games"),
    upcomingGames("upcoming-games"),
    newGames("new-games"),
    games("games"),
    upNextEpisodes("up-next-episodes"),
    episodes("episodes"),
    songs("songs"),
    characters("characters"),
    people("people"),
    genres("genres"),
    themes("themes"),
    recap("recap")
}
