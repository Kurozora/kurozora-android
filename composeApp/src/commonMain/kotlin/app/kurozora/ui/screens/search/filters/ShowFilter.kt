package app.kurozora.ui.screens.search.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kurozora.core.util.TriState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kurozorakit.data.enums.CountryOfOrigin
import kurozorakit.data.enums.SeasonOfYear
import kurozorakit.data.enums.ShowStatus
import kurozorakit.data.enums.ShowType
import kurozorakit.data.enums.SourceType
import kurozorakit.data.enums.TVRating
import kurozorakit.data.models.Filterable
import kurozorakit.data.models.search.filters.FilterValue
import kurozorakit.data.models.search.filters.ShowFilter
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

data class ShowFilterState(
    val airTime: String = "",
    val airDay: String = "",
    val airSeason: Map<SeasonOfYear, TriState> = emptyMap(),
    val country: Map<CountryOfOrigin, TriState> = emptyMap(),
    val duration: String = "",
    val isNSFW: Boolean = false,
    val startedAt: String = "",
    val endedAt: String = "",
    val mediaType: Map<ShowType, TriState> = emptyMap(),
    val source: Map<SourceType, TriState> = emptyMap(),
    val status: Map<ShowStatus, TriState> = emptyMap(),
    val tvRating: Map<TVRating, TriState> = emptyMap(),
    val seasonCount: String = "",
    val episodeCount: String = "",
)

fun <E> triStateToFilterValue(map: Map<E, TriState>, toId: (E) -> String): FilterValue? {
    val include = map.filterValues { it == TriState.INCLUDE }.keys.map(toId)
    val exclude = map.filterValues { it == TriState.EXCLUDE }.keys.map(toId)
    return if (include.isEmpty() && exclude.isEmpty()) null
    else FilterValue(
        include = include.takeIf { it.isNotEmpty() }?.joinToString(","),
        exclude = exclude.takeIf { it.isNotEmpty() }?.joinToString(",")
    )
}

class ShowFilterViewModel(
    initialFilter: ShowFilter? = null,
) : ViewModel() {
    private val _state = MutableStateFlow(ShowFilterState())
    val state: StateFlow<ShowFilterState> = _state
    fun update(transform: ShowFilterState.() -> ShowFilterState) {
        viewModelScope.launch {
            _state.value = _state.value.transform()
        }
    }

    fun toShowFilter(): ShowFilter {
        val s = _state.value

        return ShowFilter(
            airDay = s.airDay.ifBlank { null },
            airTime = s.airTime.ifBlank { null },
            airSeason = triStateToFilterValue(s.airSeason) { it.rawValue.toString() },
            countryOfOrigin = triStateToFilterValue(s.country) { it.rawValue.toString() },
            duration = s.duration.ifBlank { null },
            isNSFW = s.isNSFW,
            startedAt = s.startedAt.toLongOrNull(),
            endedAt = s.endedAt.toLongOrNull(),
            mediaType = triStateToFilterValue(s.mediaType) { it.rawValue.toString() },
            source = triStateToFilterValue(s.source) { it.rawValue.toString() },
            status = triStateToFilterValue(s.status) { it.rawValue.toString() },
            tvRating = triStateToFilterValue(s.tvRating) { it.rawValue.toString() },
            seasonCount = s.seasonCount.toIntOrNull(),
            episodeCount = s.episodeCount.toIntOrNull()
        )
    }
}

@OptIn(KoinExperimentalAPI::class)
@Composable
fun ShowFilterSection(
    filter: ShowFilter?,
    onFilterChange: (Filterable) -> Unit,
    viewModel: ShowFilterViewModel = koinViewModel(parameters = { parametersOf(filter) }),
) {
    val state by viewModel.state.collectAsState()
    fun update(block: ShowFilterState.() -> ShowFilterState) {
        viewModel.update(block)
        onFilterChange(viewModel.toShowFilter())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Anime Filters", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = state.airDay,
            onValueChange = { update { copy(airDay = it) } },
            label = { Text("Air Day") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
//        DropdownMultiSelect(
//            label = "Air Season",
//            options = DayOfWeek.allCases.toList(),
//            selectedItems = ,
//            getDisplayName = { it.displayName },
//            onSelectionChange = { update { copy(airDay = it.toString()) } }
//        )
        TimePickerExposedDropdown(
            label = "Air Time",
            time = state.airTime,
            onTimeSelected = { newTime ->
                update { copy(airTime = newTime) }
            }
        )

        TriStateCheckboxDropdown(
            label = "Air Season",
            options = SeasonOfYear.allCases.toList(),
            stateMap = state.airSeason,
            getDisplayName = { it.displayName },
            onStateChange = { update { copy(airSeason = it) } }
        )

        TriStateCheckboxDropdown(
            label = "Country of Origin",
            options = CountryOfOrigin.allCases.toList(),
            stateMap = state.country,
            getDisplayName = { it.displayName },
            onStateChange = { update { copy(country = it) } }
        )
//        DropdownMultiSelect(
//            label = "Country of Origin",
//            options = CountryOfOrigin.allCases.toList(),
//            selectedItems = state.country,
//            getDisplayName = { it.displayName },
//            onSelectionChange = { update { copy(country = it) } }
//        )
        OutlinedTextField(
            value = state.duration,
            onValueChange = { update { copy(duration = it) } },
            label = { Text("Duration (min)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("NSFW Content")
            Switch(
                checked = state.isNSFW,
                onCheckedChange = { update { copy(isNSFW = it) } }
            )
        }

        DatePickerDropdownInput(
            label = "Start Date",
            timestampSeconds = state.startedAt,
            onDateSelected = { seconds ->
                update { copy(startedAt = seconds.toString()) }
            }
        )

        DatePickerDropdownInput(
            label = "End Date",
            timestampSeconds = state.endedAt,
            onDateSelected = { seconds ->
                update { copy(endedAt = seconds.toString()) }
            }
        )

        TriStateCheckboxDropdown(
            label = "Media Type",
            options = ShowType.allCases.toList(),
            stateMap = state.mediaType,
            getDisplayName = { it.displayName },
            onStateChange = { update { copy(mediaType = it) } }
        )

        TriStateCheckboxDropdown(
            label = "Source",
            options = SourceType.allCases.toList(),
            stateMap = state.source,
            getDisplayName = { it.displayName },
            onStateChange = { update { copy(source = it) } }
        )

        TriStateCheckboxDropdown(
            label = "Status",
            options = ShowStatus.allCases.toList(),
            stateMap = state.status,
            getDisplayName = { it.displayName },
            onStateChange = { update { copy(status = it) } }
        )

        TriStateCheckboxDropdown(
            label = "TV Rating",
            options = TVRating.allCases.toList(),
            stateMap = state.tvRating,
            getDisplayName = { it.displayName },
            onStateChange = { update { copy(tvRating = it) } }
        )
//        DropdownMultiSelect(
//            label = "Media Type",
//            options = ShowType.allCases.toList(),
//            selectedItems = state.mediaType,
//            getDisplayName = { it.displayName },
//            onSelectionChange = { update { copy(mediaType = it) } }
//        )
//
//        DropdownMultiSelect(
//            label = "Source",
//            options = SourceType.allCases.toList(),
//            selectedItems = state.source,
//            getDisplayName = { it.displayName },
//            onSelectionChange = { update { copy(source = it) } }
//        )
//
//        DropdownMultiSelect(
//            label = "Status",
//            options = ShowStatus.allCases.toList(),
//            selectedItems = state.status,
//            getDisplayName = { it.displayName },
//            onSelectionChange = { update { copy(status = it) } }
//        )
//
//        DropdownMultiSelect(
//            label = "TV Rating",
//            options = TVRating.allCases.toList(),
//            selectedItems = state.tvRating,
//            getDisplayName = { it.displayName },
//            onSelectionChange = { update { copy(tvRating = it) } }
//        )
        OutlinedTextField(
            value = state.seasonCount,
            onValueChange = { update { copy(seasonCount = it) } },
            label = { Text("Season Count") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.episodeCount,
            onValueChange = { update { copy(episodeCount = it) } },
            label = { Text("Episode Count") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
