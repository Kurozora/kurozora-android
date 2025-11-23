package com.seloreis.kurozora.ui.screens.search.filters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerExposedDropdown(
    label: String,
    time: String,
    onTimeSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val (hour, minute) = if (time.contains(":")) {
        val p = time.split(":")
        Pair(p[0].toInt(), p[1].toInt())
    } else 12 to 0

    val state = rememberTimePickerState(
        initialHour = hour,
        initialMinute = minute,
        is24Hour = true
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = time,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {

            TimePicker(
                state = state,
                layoutType = TimePickerLayoutType.Horizontal,
                modifier = Modifier
//                    .scale(0.65f)
//                    .width(240.dp)
//                    .padding(4.dp)
            )

            TextButton(
                onClick = {
                    val hh = state.hour.toString().padStart(2, '0')
                    val mm = state.minute.toString().padStart(2, '0')
                    onTimeSelected("$hh:$mm:00")
                    expanded = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tamam")
            }
        }
    }
}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TimePickerDialogInput(
//    label: String,
//    time: String,
//    onTimeSelected: (String) -> Unit,
//) {
//    var openDialog by remember { mutableStateOf(false) }
//
//    OutlinedTextField(
//        value = time,
//        onValueChange = {},
//        readOnly = true,
//        label = { Text(label) },
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { openDialog = true }
//    )
//
//    if (openDialog) {
//        TimePickerDialog(
//            onDismissRequest = { openDialog = false },
//            onConfirm = { hour, minute ->
//                val hh = hour.toString().padStart(2, '0')
//                val mm = minute.toString().padStart(2, '0')
//                onTimeSelected("$hh:$mm:00")
//                openDialog = false
//            },
//            currentTime = time
//        )
//    }
//}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (Int, Int) -> Unit,
    currentTime: String
) {
    val (initialHour, initialMinute) = remember(currentTime) {
        if (currentTime.contains(":")) {
            val parts = currentTime.split(":")
            (parts[0].toIntOrNull() ?: 0) to (parts[1].toIntOrNull() ?: 0)
        } else {
            12 to 0
        }
    }

    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = { onConfirm(state.hour, state.minute) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
        text = {
            TimePicker(
                state = state,
                layoutType = TimePickerLayoutType.Vertical
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogInput(
    label: String,
    time: String,
    onTimeSelected: (String) -> Unit,
) {
    var openSheet by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = time,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { openSheet = true }
    )

    if (openSheet) {
        TimePickerBottomSheet(
            initialTime = time,
            onDismiss = { openSheet = false },
            onConfirm = { hour, minute ->
                val hh = hour.toString().padStart(2, '0')
                val mm = minute.toString().padStart(2, '0')
                onTimeSelected("$hh:$mm:00")
                openSheet = false
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerBottomSheet(
    initialTime: String,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val (initialHour, initialMinute) = remember(initialTime) {
        if (initialTime.contains(":")) {
            val parts = initialTime.split(":")
            (parts[0].toIntOrNull() ?: 0) to (parts[1].toIntOrNull() ?: 0)
        } else 12 to 0
    }

    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        TimePicker(
            state = state,
            layoutType = TimePickerLayoutType.Vertical
        )

        TextButton(
            onClick = { onConfirm(state.hour, state.minute) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("OK")
        }
    }
}
