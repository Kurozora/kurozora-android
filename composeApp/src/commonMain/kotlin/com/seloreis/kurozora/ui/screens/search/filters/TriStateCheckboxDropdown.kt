package com.seloreis.kurozora.ui.screens.search.filters

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.seloreis.kurozora.core.util.TriState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> TriStateCheckboxDropdown(
    label: String,
    options: List<T>,
    stateMap: Map<T, TriState>,
    getDisplayName: (T) -> String,
    onStateChange: (Map<T, TriState>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val included = options.filter { stateMap[it] == TriState.INCLUDE }
    val excluded = options.filter { stateMap[it] == TriState.EXCLUDE }

    val summary = buildList {
        if (included.isNotEmpty()) add("+ ${included.joinToString { getDisplayName(it) }}")
        if (excluded.isNotEmpty()) add("- ${excluded.joinToString { getDisplayName(it) }}")
    }.joinToString(" | ")

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = summary,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { item ->
                val triState = stateMap[item] ?: TriState.NONE
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TriStateCheckbox(
                                state = when (triState) {
                                    TriState.NONE -> ToggleableState.Off
                                    TriState.INCLUDE -> ToggleableState.On
                                    TriState.EXCLUDE -> ToggleableState.Indeterminate
                                },
                                onClick = {
                                    val newState = triState.next()
                                    onStateChange(stateMap + (item to newState))
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(getDisplayName(item))
                        }
                    },
                    onClick = {
                        val newState = triState.next()
                        onStateChange(stateMap + (item to newState))
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
