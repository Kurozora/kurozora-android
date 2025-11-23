package app.kurozora.ui.screens.airseason

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kurozorakit.data.enums.SeasonOfYear
import kurozorakit.data.models.show.Show

data class AirSeasonState(
    val selectedTab: SeasonTab = getCurrentSeasonTab(),
    val tabs: List<SeasonTab> = getSeasonTabs(getCurrentSeasonTab()),
    val shows: List<Show> = emptyList(),
    val next: String? = null,
    val loadingItems: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

data class SeasonTab(
    val type: SeasonTabType = SeasonTabType.Normal,
    val season: SeasonOfYear,
    val year: Int,
)

enum class SeasonTabType {
    Normal,
    Archive
}

fun getCurrentSeasonTab(): SeasonTab {
    val now = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val year = now.year
    val season = when (now.monthNumber) {
        in 3..5 -> SeasonOfYear.SPRING      // Mart–Nisan–Mayıs
        in 6..8 -> SeasonOfYear.SUMMER      // Haziran–Temmuz–Ağustos
        in 9..11 -> SeasonOfYear.FALL       // Eylül–Ekim–Kasım
        else -> SeasonOfYear.WINTER         // Aralık–Ocak–Şubat
    }

    return SeasonTab(season = season, year = year)
}

fun getSeasonTabs(center: SeasonTab): List<SeasonTab> {
    val order = listOf(
        SeasonOfYear.WINTER,
        SeasonOfYear.SPRING,
        SeasonOfYear.SUMMER,
        SeasonOfYear.FALL,
    )
    val idx = order.indexOf(center.season)
    fun shift(offset: Int): SeasonTab {
        val newIndex = idx + offset
        return if (newIndex in 0..3) {
            // Aynı yıl içinde
            SeasonTab(season = order[newIndex], year = center.year)
        } else {
            // Yıl kayması
            val yearOffset = if (newIndex < 0) -1 else 1
            val fixedIndex = (newIndex + 4) % 4
            SeasonTab(season = order[fixedIndex], year = center.year + yearOffset)
        }
    }

    SeasonTab(
        type = SeasonTabType.Archive,
        season = SeasonOfYear.WINTER,
        year = 0
    )

    return listOf(
        shift(-1), // previous
        center,    // selected
        shift(+1), // next
        shift(+2),  // next next
    )
}
