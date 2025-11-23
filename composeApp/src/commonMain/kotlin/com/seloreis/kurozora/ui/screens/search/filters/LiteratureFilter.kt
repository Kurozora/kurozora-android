package com.seloreis.kurozora.ui.screens.search.filters

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seloreis.kurozora.core.util.TriState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kurozorakit.data.enums.*
import kurozorakit.data.models.Filterable
import kurozorakit.data.models.search.filters.LiteratureFilter
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

// --- STATE ---
data class LiteratureFilterState(
    val publicationDay: String = "",
    val publicationTime: String = "",
    val publicationSeason:  Map<SeasonOfYear, TriState> = emptyMap(),
    val countryOfOrigin: Map<CountryOfOrigin, TriState> = emptyMap(),
    val duration: String = "",
    val startedAt: String = "",
    val endedAt: String = "",
    val isNSFW: Boolean = false,
    val mediaType: Map<LiteratureType, TriState> = emptyMap(),
    val source: Map<SourceType, TriState> = emptyMap(),
    val status: Map<LiteratureStatus, TriState> = emptyMap(),
    val tvRating: Map<TVRating, TriState> = emptyMap(),
    val volumeCount: String = "",
    val chapterCount: String = "",
    val pageCount: String = ""
)

// --- VIEWMODEL ---
class LiteratureFilterViewModel(
    initialFilter: LiteratureFilter? = null
) : ViewModel() {

    private val _state = MutableStateFlow(LiteratureFilterState())
    val state: StateFlow<LiteratureFilterState> = _state

    fun update(block: LiteratureFilterState.() -> LiteratureFilterState) {
        viewModelScope.launch { _state.value = _state.value.block() }
    }

    fun toLiteratureFilter(): LiteratureFilter {
        val s = _state.value
        return LiteratureFilter(
            publicationDay = s.publicationDay.ifBlank { null },
            publicationSeason = triStateToFilterValue(s.publicationSeason) { it.rawValue.toString() },
            countryOfOrigin = triStateToFilterValue(s.countryOfOrigin) { it.rawValue.toString() },
            duration = s.duration.toIntOrNull(),
            startedAt = s.startedAt.toLongOrNull(),
            endedAt = s.endedAt.toLongOrNull(),
            isNSFW = s.isNSFW,
            mediaType = triStateToFilterValue(s.mediaType) { it.rawValue.toString() },
            source = triStateToFilterValue(s.source) { it.rawValue.toString() },
            status = triStateToFilterValue(s.status) { it.rawValue.toString() },
            tvRating = triStateToFilterValue(s.tvRating) { it.rawValue.toString() },
            volumeCount = s.volumeCount.toIntOrNull(),
            chapterCount = s.chapterCount.toIntOrNull(),
            pageCount = s.pageCount.toIntOrNull()
        )
    }
}

// --- UI ---
@OptIn(KoinExperimentalAPI::class)
@Composable
fun LiteratureFilterSection(
    filter: LiteratureFilter?,
    onFilterChange: (Filterable) -> Unit,
    viewModel: LiteratureFilterViewModel = koinViewModel(parameters = { parametersOf(filter) })
) {
    val state by viewModel.state.collectAsState()

    fun update(block: LiteratureFilterState.() -> LiteratureFilterState) {
        viewModel.update(block)
        onFilterChange(viewModel.toLiteratureFilter())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Literature Filters", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = state.publicationDay,
            onValueChange = { update { copy(publicationDay = it) } },
            label = { Text("Publication Day") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.publicationTime,
            onValueChange = { update { copy(publicationTime = it) } },
            label = { Text("Publication Time") },
            modifier = Modifier.fillMaxWidth()
        )

        TriStateCheckboxDropdown(
            label = "Publication Season",
            options = SeasonOfYear.allCases.toList(),
            stateMap = state.publicationSeason,
            getDisplayName = { it.displayName },
            onStateChange = { update { copy(publicationSeason = it) } }
        )

        TriStateCheckboxDropdown(
            label = "Country of Origin",
            options = CountryOfOrigin.allCases.toList(),
            stateMap = state.countryOfOrigin,
            getDisplayName = { it.displayName },
            onStateChange = { update { copy(countryOfOrigin = it) } }
        )

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

        OutlinedTextField(
            value = state.startedAt,
            onValueChange = { update { copy(startedAt = it) } },
            label = { Text("Start Date (timestamp)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.endedAt,
            onValueChange = { update { copy(endedAt = it) } },
            label = { Text("End Date (timestamp)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        TriStateCheckboxDropdown(
            label = "Media Type",
            options = LiteratureType.allCases.toList(),
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
            options = LiteratureStatus.allCases.toList(),
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

        OutlinedTextField(
            value = state.volumeCount,
            onValueChange = { update { copy(volumeCount = it) } },
            label = { Text("Volume Count") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.chapterCount,
            onValueChange = { update { copy(chapterCount = it) } },
            label = { Text("Chapter Count") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.pageCount,
            onValueChange = { update { copy(pageCount = it) } },
            label = { Text("Page Count") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
