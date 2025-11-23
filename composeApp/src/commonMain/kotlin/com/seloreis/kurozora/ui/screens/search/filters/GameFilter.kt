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
import kurozorakit.data.models.search.filters.GameFilter
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

// --- STATE ---
data class GameFilterState(
    val publicationDay: String = "",
    val publicationSeason: Map<SeasonOfYear, TriState> = emptyMap(),
    val countryOfOrigin: Map<CountryOfOrigin, TriState> = emptyMap(),
    val duration: String = "",
    val publishedAt: String = "",
    val isNSFW: Boolean = false,
    val mediaType: Map<GameType, TriState> = emptyMap(),
    val source: Map<SourceType, TriState> = emptyMap(),
    val status: Map<GameStatus, TriState> = emptyMap(),
    val tvRating: Map<TVRating, TriState> = emptyMap(),
    val editionCount: String = ""
)

// --- VIEWMODEL ---
class GameFilterViewModel(
    initialFilter: GameFilter? = null
) : ViewModel() {

    private val _state = MutableStateFlow(GameFilterState())
    val state: StateFlow<GameFilterState> = _state

    fun update(block: GameFilterState.() -> GameFilterState) {
        viewModelScope.launch { _state.value = _state.value.block() }
    }

    fun toGameFilter(): GameFilter {
        val s = _state.value
        return GameFilter(
            publicationDay = s.publicationDay.toIntOrNull(),
            publicationSeason = triStateToFilterValue(s.publicationSeason) { it.rawValue.toString() },
            countryOfOrigin = triStateToFilterValue(s.countryOfOrigin) { it.rawValue.toString() },
            duration = s.duration.toIntOrNull(),
            publishedAt = s.publishedAt.toLongOrNull(),
            isNSFW = s.isNSFW,
            mediaType = triStateToFilterValue(s.mediaType) { it.rawValue.toString() },
            source = triStateToFilterValue(s.source) { it.rawValue.toString() },
            status = triStateToFilterValue(s.status) { it.rawValue.toString() },
            tvRating = triStateToFilterValue(s.tvRating) { it.rawValue.toString() },
            editionCount = s.editionCount.toIntOrNull()
        )
    }
}

// --- UI ---
@OptIn(KoinExperimentalAPI::class)
@Composable
fun GameFilterSection(
    filter: GameFilter?,
    onFilterChange: (Filterable) -> Unit,
    viewModel: GameFilterViewModel = koinViewModel(parameters = { parametersOf(filter) })
) {
    val state by viewModel.state.collectAsState()

    fun update(block: GameFilterState.() -> GameFilterState) {
        viewModel.update(block)
        onFilterChange(viewModel.toGameFilter())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Game Filters", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = state.publicationDay,
            onValueChange = { update { copy(publicationDay = it) } },
            label = { Text("Publication Day") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

        OutlinedTextField(
            value = state.publishedAt,
            onValueChange = { update { copy(publishedAt = it) } },
            label = { Text("Published At (timestamp)") },
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

            TriStateCheckboxDropdown(
                label = "Media Type",
                options = GameType.allCases.toList(),
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
                options = GameStatus.allCases.toList(),
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
                value = state.editionCount,
                onValueChange = { update { copy(editionCount = it) } },
                label = { Text("Edition Count") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
