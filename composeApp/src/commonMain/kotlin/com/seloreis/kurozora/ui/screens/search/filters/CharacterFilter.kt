package com.seloreis.kurozora.ui.screens.search.filters

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kurozorakit.data.models.Filterable
import kurozorakit.data.models.search.filters.CharacterFilter
import org.koin.compose.viewmodel.koinViewModel
import androidx.lifecycle.ViewModel
import com.seloreis.kurozora.core.util.TriState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kurozorakit.data.enums.AstrologicalSign
import kurozorakit.data.enums.CharacterStatus
import kurozorakit.data.enums.CountryOfOrigin
import kurozorakit.data.enums.SeasonOfYear
import org.koin.core.parameter.parametersOf
import kotlin.collections.isNotEmpty
import kotlin.collections.joinToString


data class CharacterFilterState(
    val age: String = "",
    val astrologicalSign: Map<AstrologicalSign, TriState> = emptyMap(),
    val birthDay: String = "",
    val birthMonth: String = "",
    val bust: String = "",
    val height: String = "",
    val hip: String = "",
    val status: List<CharacterStatus> = emptyList(),
    val waist: String = "",
    val weight: String = ""
) {
    companion object {
        fun from(filter: CharacterFilter?) = CharacterFilterState(
            age = filter?.age?.toString() ?: "",
            astrologicalSign = emptyMap(),
            birthDay = filter?.birthDay?.toString() ?: "",
            birthMonth = filter?.birthMonth?.toString() ?: "",
            bust = filter?.bust.toString(),
            height = filter?.height.toString(),
            hip = filter?.hip.toString(),
            status = emptyList(),
            waist = filter?.waist.toString(),
            weight = filter?.weight.toString()
        )
    }
}


class CharacterFilterViewModel(
    filter: CharacterFilter? = null
) : ViewModel() {

    private val _state = MutableStateFlow(CharacterFilterState.from(filter))
    val uiState = _state.asStateFlow()

    fun update(block: CharacterFilterState.() -> CharacterFilterState) {
        _state.value = _state.value.block()
    }

    fun setFilter(filter: CharacterFilter) {
        _state.value = CharacterFilterState.from(filter)
    }

    fun toCharacterFilter(): CharacterFilter {
        val s = _state.value
        return CharacterFilter(
            age = s.age.toIntOrNull(),
            astrologicalSign = triStateToFilterValue(s.astrologicalSign) { it.rawValue.toString() },
            birthDay = s.birthDay.toIntOrNull(),
            birthMonth = s.birthMonth.toIntOrNull(),
            bust = s.bust,
            height = s.height,
            hip = s.hip,
            status = s.status.takeIf { it.isNotEmpty() }?.joinToString(",") { it.rawValue.toString() },
            waist = s.waist,
            weight = s.weight
        )
    }
}


@Composable
fun CharacterFilterSection(
    filter: CharacterFilter?,
    onFilterChange: (Filterable) -> Unit,
    viewModel: CharacterFilterViewModel = koinViewModel(parameters = { parametersOf(filter) })
) {
    LaunchedEffect(filter) {
        if (filter != null) viewModel.setFilter(filter)
    }

    val state by viewModel.uiState.collectAsState()

    fun update(block: CharacterFilterState.() -> CharacterFilterState) {
        viewModel.update(block)
        onFilterChange(viewModel.toCharacterFilter())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = state.age?.toString() ?: "",
            onValueChange = { update { copy(age = it) } },
            label = { Text("Age") },
            modifier = Modifier.fillMaxWidth()
        )

//        DropdownMultiSelect(
//            label = "Astrological Sign",
//            options = AstrologicalSign.allCases.toList(),
//            selectedItems = state.astrologicalSign,
//            getDisplayName = { it.title },
//            onSelectionChange = { update { copy(astrologicalSign = it) } }
//        )
        TriStateCheckboxDropdown(
            label = "Astrological Sign",
            options = AstrologicalSign.allCases.toList(),
            stateMap = state.astrologicalSign,
            getDisplayName = { it.title },
            onStateChange = { update { copy(astrologicalSign = it) } }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = state.birthMonth,
                onValueChange = { update { copy(birthMonth = it) } },
                label = { Text("Birth Month") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = state.birthDay,
                onValueChange = { update { copy(birthDay = it) } },
                label = { Text("Birth Day") },
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = state.height,
            onValueChange = { update { copy(height = it) } },
            label = { Text("Height") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.weight,
            onValueChange = { update { copy(weight = it) } },
            label = { Text("Weight") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = state.bust,
                onValueChange = { update { copy(bust = it) } },
                label = { Text("Bust") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = state.waist,
                onValueChange = { update { copy(waist = it) } },
                label = { Text("Waist") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = state.hip,
                onValueChange = { update { copy(hip = it) } },
                label = { Text("Hip") },
                modifier = Modifier.weight(1f)
            )
        }

        DropdownMultiSelect(
            label = "Status",
            options = CharacterStatus.allCases.toList(),
            selectedItems = state.status,
            getDisplayName = { it.title },
            onSelectionChange = { update { copy(status = it) } }
        )
    }
}
