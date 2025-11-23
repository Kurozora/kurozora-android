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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kurozorakit.data.enums.StudioType
import kurozorakit.data.enums.TVRating
import kurozorakit.data.models.Filterable
import kurozorakit.data.models.search.filters.StudioFilter
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

// --- STATE ---
data class StudioFilterState(
    val type: List<StudioType> = emptyList(),
    val tvRating: List<TVRating> = emptyList(),
    val address: String = "",
    val foundedAt: String = "",
    val defunctAt: String = "",
    val isNSFW: Boolean = false,
)

// --- VIEWMODEL ---
class StudioFilterViewModel(
    initialFilter: StudioFilter? = null,
) : ViewModel() {
    private val _state = MutableStateFlow(
        StudioFilterState(
            type = emptyList(),
            tvRating = emptyList(),
            address = initialFilter?.address ?: "",
            foundedAt = initialFilter?.foundedAt?.toString() ?: "",
            defunctAt = initialFilter?.defunctAt?.toString() ?: "",
            isNSFW = initialFilter?.isNSFW ?: false
        )
    )
    val state: StateFlow<StudioFilterState> = _state
    fun update(block: StudioFilterState.() -> StudioFilterState) {
        viewModelScope.launch { _state.value = _state.value.block() }
    }

    fun toStudioFilter(): StudioFilter {
        val s = _state.value
        return StudioFilter(
            type = s.type.takeIf { it.isNotEmpty() }
                ?.joinToString(",") { it.rawValue.toString() },
            tvRating = s.tvRating.takeIf { it.isNotEmpty() }
                ?.joinToString(",") { it.rawValue.toString() },
            address = s.address.ifBlank { null },
            foundedAt = s.foundedAt.toLongOrNull(),
            defunctAt = s.defunctAt.toLongOrNull(),
            isNSFW = s.isNSFW.takeIf { it }
        )
    }
}

// --- UI ---
@OptIn(KoinExperimentalAPI::class)
@Composable
fun StudioFilterSection(
    filter: StudioFilter?,
    onFilterChange: (Filterable) -> Unit,
    viewModel: StudioFilterViewModel = koinViewModel(parameters = { parametersOf(filter) }),
) {
    val state by viewModel.state.collectAsState()
    fun update(block: StudioFilterState.() -> StudioFilterState) {
        viewModel.update(block)
        onFilterChange(viewModel.toStudioFilter())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Studio Filters", style = MaterialTheme.typography.titleMedium)
        // Multi-select dropdowns
        DropdownMultiSelect(
            label = "Studio Type",
            options = StudioType.allCases.toList(),
            selectedItems = state.type,
            getDisplayName = { it.displayName },
            onSelectionChange = { update { copy(type = it) } }
        )

        DropdownMultiSelect(
            label = "TV Rating",
            options = TVRating.allCases.toList(),
            selectedItems = state.tvRating,
            getDisplayName = { it.displayName },
            onSelectionChange = { update { copy(tvRating = it) } }
        )
        // Text field
        OutlinedTextField(
            value = state.address,
            onValueChange = { update { copy(address = it) } },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )
        // Date fields
        OutlinedTextField(
            value = state.foundedAt,
            onValueChange = { update { copy(foundedAt = it) } },
            label = { Text("Founded Date (timestamp)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.defunctAt,
            onValueChange = { update { copy(defunctAt = it) } },
            label = { Text("Defunct Date (timestamp)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        // Boolean switch
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
    }
}
