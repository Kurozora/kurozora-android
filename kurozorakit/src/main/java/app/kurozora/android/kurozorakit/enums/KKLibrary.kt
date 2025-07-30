package app.kurozora.android.kurozorakit.enums

import kotlinx.serialization.Serializable

object KKLibrary {
    // MARK: - Kind
    @Serializable
    enum class Kind(val value: Int) {
        shows(0),
        literatures(1),
        games(2);

        val stringValue: String
            get() = when (this) {
                shows -> "Shows"
                literatures -> "Literatures"
                games -> "Games"
            }
    }

    // MARK: - Status
    @Serializable
    enum class Status(val value: Int) {
        none(-1),
        inProgress(0),
        planning(2),
        completed(3),
        onHold(4),
        dropped(1),
        interested(6),
        ignored(5);

        val stringValue: String
            get() = when (this) {
                none -> "None"
                inProgress -> "In Progress"
                planning -> "Planning"
                completed -> "Completed"
                onHold -> "On-Hold"
                dropped -> "Dropped"
                interested -> "Interested"
                ignored -> "Ignored"
            }
        val sectionValue: String
            get() = when (this) {
                inProgress -> "InProgress"
                onHold -> "OnHold"
                else -> stringValue
            }

        companion object {
            val all =
                listOf(inProgress, planning, completed, onHold, dropped, interested, ignored)

            fun fromInt(value: Int): Status? = values().find { it.value == value }
        }
    }

    // MARK: - SortType
    @Serializable
    enum class SortType(val value: Int) {
        none(0),
        alphabetically(1),
        popularity(2),
        date(3),
        rating(4),
        myRating(5);

        val stringValue: String
            get() = when (this) {
                none -> "None"
                alphabetically -> "Alphabetically"
                popularity -> "Popularity"
                date -> "Date"
                rating -> "Rating"
                myRating -> "My Rating"
            }
        val parameterValue: String
            get() = when (this) {
                none -> ""
                alphabetically -> "title"
                popularity -> "popularity"
                date -> "date"
                rating -> "rating"
                myRating -> "my-rating"
            }
        val optionValue: List<Option>
            get() = when (this) {
                none -> emptyList()
                alphabetically -> listOf(Option.ascending, Option.descending)
                popularity -> listOf(Option.most, Option.least)
                date -> listOf(Option.newest, Option.oldest)
                rating, myRating -> listOf(Option.best, Option.worst)
            }

        companion object {
            val all = listOf(alphabetically, popularity, date, rating, myRating)
            fun fromInt(value: Int): SortType? = values().find { it.value == value }
        }
    }

    // MARK: - SortType Option
    @Serializable
    enum class Option(val value: Int) {
        none(0),
        ascending(1),
        descending(2),
        most(3),
        least(4),
        newest(5),
        oldest(6),
        worst(7),
        best(8);

        val stringValue: String
            get() = when (this) {
                none -> "None"
                ascending -> "A-Z"
                descending -> "Z-A"
                most -> "Most"
                least -> "Least"
                newest -> "Newest"
                oldest -> "Oldest"
                worst -> "Worst"
                best -> "Best"
            }
        val parameterValue: String
            get() = when (this) {
                none -> "()"
                ascending -> "(asc)"
                descending -> "(desc)"
                most -> "(most)"
                least -> "(least)"
                newest -> "(newest)"
                oldest -> "(oldest)"
                best -> "(best)"
                worst -> "(worst)"
            }

        companion object {
            val all = listOf(ascending, descending, newest, oldest, worst, best)
            fun fromInt(value: Int): Option? = values().find { it.value == value }
        }
    }
}
