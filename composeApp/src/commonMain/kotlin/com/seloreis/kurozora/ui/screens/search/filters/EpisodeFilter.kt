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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kurozorakit.data.enums.SeasonOfYear
import kurozorakit.data.enums.TVRating
import kurozorakit.data.models.Filterable
import kurozorakit.data.models.search.filters.EpisodeFilter
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

// --- STATE ---
data class EpisodeFilterState(
    val duration: String = "",
    val isFiller: Boolean = false,
    val isNSFW: Boolean = false,
    val isSpecial: Boolean = false,
    val isPremiere: Boolean = false,
    val isFinale: Boolean = false,
    val number: String = "",
    val numberTotal: String = "",
    val season: List<SeasonOfYear> = emptyList(),
    val tvRating: List<TVRating> = emptyList(),
    val startedAt: String = "",
    val endedAt: String = ""
)

// --- VIEWMODEL ---
class EpisodeFilterViewModel(
    initialFilter: EpisodeFilter? = null
) : ViewModel() {

    private val _state = MutableStateFlow(
        EpisodeFilterState(
            duration = initialFilter?.duration?.toString() ?: "",
            isFiller = initialFilter?.isFiller ?: false,
            isNSFW = initialFilter?.isNSFW ?: false,
            isSpecial = initialFilter?.isSpecial ?: false,
            isPremiere = initialFilter?.isPremiere ?: false,
            isFinale = initialFilter?.isFinale ?: false,
            number = initialFilter?.number?.toString() ?: "",
            numberTotal = initialFilter?.numberTotal?.toString() ?: "",
            season = emptyList(),
            tvRating = emptyList(),
            startedAt = initialFilter?.startedAt?.toString() ?: "",
            endedAt = initialFilter?.endedAt?.toString() ?: ""
        )
    )
    val state: StateFlow<EpisodeFilterState> = _state

    fun update(block: EpisodeFilterState.() -> EpisodeFilterState) {
        viewModelScope.launch { _state.value = _state.value.block() }
    }

    fun toEpisodeFilter(): EpisodeFilter {
        val s = _state.value
        return EpisodeFilter(
            duration = s.duration.toIntOrNull(),
            isFiller = s.isFiller.takeIf { it },
            isNSFW = s.isNSFW.takeIf { it },
            isSpecial = s.isSpecial.takeIf { it },
            isPremiere = s.isPremiere.takeIf { it },
            isFinale = s.isFinale.takeIf { it },
            number = s.number.toIntOrNull(),
            numberTotal = s.numberTotal.toIntOrNull(),
            season = s.season.takeIf { it.isNotEmpty() }
                ?.joinToString(",") { it.rawValue.toString() },
            tvRating = s.tvRating.takeIf { it.isNotEmpty() }
                ?.joinToString(",") { it.rawValue.toString() },
            startedAt = s.startedAt.toLongOrNull(),
            endedAt = s.endedAt.toLongOrNull()
        )
    }
}

// --- UI ---
@OptIn(KoinExperimentalAPI::class)
@Composable
fun EpisodeFilterSection(
    filter: EpisodeFilter?,
    onFilterChange: (Filterable) -> Unit,
    viewModel: EpisodeFilterViewModel = koinViewModel(parameters = { parametersOf(filter) })
) {
    val state by viewModel.state.collectAsState()

    fun update(block: EpisodeFilterState.() -> EpisodeFilterState) {
        viewModel.update(block)
        onFilterChange(viewModel.toEpisodeFilter())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Episode Filters", style = MaterialTheme.typography.titleMedium)

        // Numeric fields
        OutlinedTextField(
            value = state.duration,
            onValueChange = { update { copy(duration = it) } },
            label = { Text("Duration (min)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.number,
            onValueChange = { update { copy(number = it) } },
            label = { Text("Episode Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.numberTotal,
            onValueChange = { update { copy(numberTotal = it) } },
            label = { Text("Total Episodes") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMultiSelect(
            label = "Season",
            options = SeasonOfYear.allCases.toList(),
            selectedItems = state.season,
            getDisplayName = { it.displayName },
            onSelectionChange = { update { copy(season = it) } }
        )

        // Boolean switches
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Filler Episode")
            Switch(
                checked = state.isFiller,
                onCheckedChange = { update { copy(isFiller = it) } }
            )
        }

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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Special Episode")
            Switch(
                checked = state.isSpecial,
                onCheckedChange = { update { copy(isSpecial = it) } }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Premiere Episode")
            Switch(
                checked = state.isPremiere,
                onCheckedChange = { update { copy(isPremiere = it) } }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Finale Episode")
            Switch(
                checked = state.isFinale,
                onCheckedChange = { update { copy(isFinale = it) } }
            )
        }

        // Date fields
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

        // Multi-select dropdown
        DropdownMultiSelect(
            label = "TV Rating",
            options = TVRating.allCases.toList(),
            selectedItems = state.tvRating,
            getDisplayName = { it.displayName },
            onSelectionChange = { update { copy(tvRating = it) } }
        )
    }
}