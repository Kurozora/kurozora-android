package com.seloreis.kurozora.ui.screens.search.filters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDropdownInput(
    label: String,
    timestampSeconds: String,
    onDateSelected: (Long) -> Unit
) {
    var openDialog by remember { mutableStateOf(false) }

    // Dropdown UI (sadece tetikler)
    ExposedDropdownMenuBox(
        expanded = false,
        onExpandedChange = {
            openDialog = true // ❗ dialog'u tetikleyen tek şey bu
        }
    ) {
        val formattedDate = remember(timestampSeconds) {
            timestampSeconds.toLongOrNull()?.let { seconds ->
                val instant = Instant.fromEpochSeconds(seconds)
                val date = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date

                val day = date.dayOfMonth.toString().padStart(2, '0')
                val month = date.monthNumber.toString().padStart(2, '0')
                val year = date.year.toString()

                "$day.$month.$year"
            } ?: ""
        }

        OutlinedTextField(
            value = formattedDate,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
    }

    // ❗❗ DİKKAT: Dialog dropdown'ın DIŞINDA
    if (openDialog) {
        AppDatePickerDialog(
            onDismiss = { openDialog = false },
            onDateSelected = {
                onDateSelected(it)
                openDialog = false
            }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDatePickerDialog(
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    val state = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let {
                    onDateSelected(it / 1000)
                }
                onDismiss()
            }) {
                Text("OK")
            }
        }
    ) {
        DatePicker(state = state)
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogInput(
    label: String,
    timestampSeconds: String,
    onDateSelected: (Long) -> Unit
) {
    var openDialog by remember { mutableStateOf(false) }

    // Timestamp'i okunabilir formatta göster
    val formattedDate = remember(timestampSeconds) {
        timestampSeconds.toLongOrNull()?.let { seconds ->
            val instant = Instant.fromEpochSeconds(seconds)
            instant.toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        } ?: ""
    }

    OutlinedTextField(
        value = formattedDate,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { openDialog = true }
    )

    if (openDialog) {
        DatePickerDialog(
            onDismissRequest = { openDialog = false },
            confirmButton = {
                TextButton(onClick = { openDialog = false }) {
                    Text("OK")
                }
            }
        ) {
            val state = rememberDatePickerState()

            DatePicker(state = state)

            // DatePicker seçilince otomatik tetiklenir
            LaunchedEffect(state.selectedDateMillis) {
                val millis = state.selectedDateMillis
                if (millis != null) {
                    val seconds = millis / 1000
                    onDateSelected(seconds) // 👈 artık saniye formatı
                }
            }
        }
    }
}
