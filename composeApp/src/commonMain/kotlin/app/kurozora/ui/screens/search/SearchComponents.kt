package app.kurozora.ui.screens.search.filters

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import app.kurozora.ui.components.cards.MediaCardViewMode
import kurozorakit.data.enums.KKLibrary
import kurozorakit.data.enums.KKSearchType
import kurozorakit.data.models.Filterable
import kurozorakit.data.models.search.filters.CharacterFilter
import kurozorakit.data.models.search.filters.EpisodeFilter
import kurozorakit.data.models.search.filters.GameFilter
import kurozorakit.data.models.search.filters.LiteratureFilter
import kurozorakit.data.models.search.filters.PersonFilter
import kurozorakit.data.models.search.filters.ShowFilter
import kurozorakit.data.models.search.filters.StudioFilter

@Composable
fun FilterBottomSheet(
    activeType: KKSearchType?,
    activeFilter: Filterable?,
    onFilterChange: (Filterable) -> Unit,
    onApply: () -> Unit,
    mediaCard: MediaCardViewMode,
    onCardViewModeChange: (MediaCardViewMode) -> Unit,
    columnCount: Int,
    onColumnCountChange: (Int) -> Unit,
    sortType: KKLibrary.SortType,
    sortOption: KKLibrary.Option,
    applySort: (KKLibrary.SortType, KKLibrary.Option) -> Unit,
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Filter", "Sort", "View")

    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTab == index,
                    onClick = { selectedTab = index }
                )
            }
        }

        Button(onClick = onApply, modifier = Modifier.align(Alignment.End)) {
            Text("Apply")
        }
        Spacer(Modifier.height(16.dp))

        when (selectedTab) {
            0 -> FilterTabContent(activeType, activeFilter, onFilterChange)
            1 -> SortTabContent(
                type = activeType,
                selectedSortType = sortType,
                selectedOption = sortOption,
                applySort = applySort
            )
            2 -> ViewTabContent(
                type = activeType,
                cardViewMode = mediaCard,
                onCardViewModeChange = onCardViewModeChange,
                columnCount = columnCount,
                onColumnCountChange = onColumnCountChange,
            )
        }
    }
}

@Composable
fun FilterTabContent(
    activeType: KKSearchType?,
    currentFilter: Filterable?,
    onApply: (Filterable) -> Unit,
) {
    when (activeType) {
        KKSearchType.shows -> ShowFilterSection(currentFilter as? ShowFilter, onApply)
        KKSearchType.literatures -> LiteratureFilterSection(currentFilter as? LiteratureFilter, onApply)
        KKSearchType.games -> GameFilterSection(currentFilter as? GameFilter, onApply)
        KKSearchType.characters -> CharacterFilterSection(currentFilter as? CharacterFilter, onApply)
        KKSearchType.episodes -> EpisodeFilterSection(currentFilter as? EpisodeFilter, onApply)
        KKSearchType.people -> PersonFilterSection(currentFilter as? PersonFilter, onApply)
        KKSearchType.studios -> StudioFilterSection(currentFilter as? StudioFilter, onApply)
        else -> Text("No filter options available.")
    }
}

@Composable
fun SortTabContent(
    type: KKSearchType?,
    selectedSortType: KKLibrary.SortType,
    selectedOption: KKLibrary.Option,
    applySort: (KKLibrary.SortType, KKLibrary.Option) -> Unit,
) {
    if (type == null) {
        Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
            Text("No type selected for sorting.", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Sort options for $type",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        KKLibrary.SortType.all.forEach { sortType ->
            Text(
                text = sortType.stringValue,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            sortType.optionValue.forEach { option ->
                val isSelected = selectedSortType == sortType && selectedOption == option

                SortCheckboxRow(
                    sortType = sortType,
                    option = option,
                    selected = isSelected,
                    onClick = {
                        if (isSelected) {
                            applySort(KKLibrary.SortType.NONE, KKLibrary.Option.NONE)
                        } else {
                            applySort(sortType, option)
                        }
                    }
                )
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun SortCheckboxRow(
    sortType: KKLibrary.SortType,
    option: KKLibrary.Option,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val icon = iconForSort(sortType, option)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .border(
                    width = 2.dp,
                    color = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(6.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = option.stringValue,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) MaterialTheme.colorScheme.primary
            else LocalContentColor.current
        )
    }
}

@Composable
fun iconForSort(sortType: KKLibrary.SortType, option: KKLibrary.Option): ImageVector {
    return when (sortType) {
        KKLibrary.SortType.ALPHABETICALLY ->
            when (option) {
                KKLibrary.Option.ASCENDING -> Icons.Default.KeyboardArrowUp
                KKLibrary.Option.DESCENDING -> Icons.Default.KeyboardArrowDown
                else -> Icons.Default.Sort
            }

        KKLibrary.SortType.POPULARITY ->
            when (option) {
                KKLibrary.Option.MOST -> Icons.Default.Whatshot
                KKLibrary.Option.LEAST -> Icons.Default.ThumbDown
                else -> Icons.Default.Whatshot
            }

        KKLibrary.SortType.DATE ->
            when (option) {
                KKLibrary.Option.NEWEST -> Icons.Default.CalendarMonth
                KKLibrary.Option.OLDEST -> Icons.Default.Event
                else -> Icons.Default.CalendarToday
            }

        KKLibrary.SortType.RATING ->
            when (option) {
                KKLibrary.Option.BEST -> Icons.Default.Star
                KKLibrary.Option.WORST -> Icons.Default.StarBorder
                else -> Icons.Default.StarHalf
            }

        KKLibrary.SortType.MYRATING ->
            when (option) {
                KKLibrary.Option.BEST -> Icons.Default.ThumbUp
                KKLibrary.Option.WORST -> Icons.Default.ThumbDown
                else -> Icons.Default.Person
            }

        else -> Icons.Default.Help
    }
}

@Composable
fun ViewTabContent(
    type: KKSearchType?,
    cardViewMode: MediaCardViewMode,
    onCardViewModeChange: (MediaCardViewMode) -> Unit,
    columnCount: Int,
    onColumnCountChange: (Int) -> Unit,
) {
    if (type == null) {
        Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
            Text("No type selected for view settings.", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }

    val allowedModes = listOf(
        MediaCardViewMode.List,
        MediaCardViewMode.Compact,
        MediaCardViewMode.Detailed
    )

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "View mode settings for $type",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            allowedModes.forEach { mode ->
                FilterChip(
                    selected = cardViewMode == mode,
                    onClick = { onCardViewModeChange(mode) },
                    label = { Text(mode.name) }
                )
            }
        }

        if (cardViewMode == MediaCardViewMode.Compact) {
            Column(
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Text(
                    text = "Columns: $columnCount",
                    style = MaterialTheme.typography.bodyMedium
                )

                Slider(
                    value = columnCount.toFloat(),
                    onValueChange = { onColumnCountChange(it.toInt()) },
                    valueRange = 1f..10f,
                    steps = 8
                )
            }
        }
    }
}