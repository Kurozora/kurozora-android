package app.kurozora.ui.screens.search.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import kurozorakit.data.enums.AstrologicalSign
import kurozorakit.data.models.Filterable
import kurozorakit.data.models.search.filters.PersonFilter
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

// --- STATE ---
data class PersonFilterState(
    val astrologicalSign: Map<AstrologicalSign, TriState> = emptyMap(),
    val birthDate: String = "",
    val deceasedDate: String = "",
)

// --- VIEWMODEL ---
class PersonFilterViewModel(
    initialFilter: PersonFilter? = null,
) : ViewModel() {
    private val _state = MutableStateFlow(
        PersonFilterState(
            astrologicalSign = emptyMap(),
            birthDate = initialFilter?.birthDate?.toString() ?: "",
            deceasedDate = initialFilter?.deceasedDate?.toString() ?: ""
        )
    )
    val state: StateFlow<PersonFilterState> = _state
    fun update(block: PersonFilterState.() -> PersonFilterState) {
        viewModelScope.launch { _state.value = _state.value.block() }
    }

    fun toPersonFilter(): PersonFilter {
        val s = _state.value
        return PersonFilter(
            astrologicalSign = triStateToFilterValue(s.astrologicalSign) { it.rawValue.toString() },
            birthDate = s.birthDate.toLongOrNull(),
            deceasedDate = s.deceasedDate.toLongOrNull()
        )
    }
}

// --- UI ---
@OptIn(KoinExperimentalAPI::class)
@Composable
fun PersonFilterSection(
    filter: PersonFilter?,
    onFilterChange: (Filterable) -> Unit,
    viewModel: PersonFilterViewModel = koinViewModel(parameters = { parametersOf(filter) }),
) {
    val state by viewModel.state.collectAsState()
    fun update(block: PersonFilterState.() -> PersonFilterState) {
        viewModel.update(block)
        onFilterChange(viewModel.toPersonFilter())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Person Filters", style = MaterialTheme.typography.titleMedium)
        // Multi-select dropdown for astrological sign
        TriStateCheckboxDropdown(
            label = "Astrological Sign",
            options = AstrologicalSign.allCases.toList(),
            stateMap = state.astrologicalSign,
            getDisplayName = { it.title },
            onStateChange = { update { copy(astrologicalSign = it) } }
        )
        // Date fields
        OutlinedTextField(
            value = state.birthDate,
            onValueChange = { update { copy(birthDate = it) } },
            label = { Text("Birth Date (timestamp)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.deceasedDate,
            onValueChange = { update { copy(deceasedDate = it) } },
            label = { Text("Deceased Date (timestamp)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
