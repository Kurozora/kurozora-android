package com.seloreis.kurozora.ui.screens.schedule

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.seloreis.kurozora.core.util.InstantUtils.toHourMinute
import com.seloreis.kurozora.ui.components.ItemList
import com.seloreis.kurozora.ui.components.ItemListViewMode
import com.seloreis.kurozora.ui.components.cards.AnimeCard
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    windowWidth: WindowWidthSizeClass,
    onNavigateToItemDetail: (Any) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ScheduleViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Schedule",
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Geri"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Gün filter chipler
            DayFilterChips { selectedDay ->
                viewModel.fetchScheduleForDay(selectedDay)
            }
            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                state.errorMessage != null -> {
                    Text(
                        "Error: ${state.errorMessage}",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val columnCount = when (windowWidth) {
                            WindowWidthSizeClass.COMPACT -> 1   // 📱 Telefon
                            WindowWidthSizeClass.MEDIUM -> 3    // 💻 Küçük tablet
                            WindowWidthSizeClass.EXPANDED -> 4  // 🖥️ Büyük ekran
                            else -> 3
                        }

                        if (state.shows.isNotEmpty()) {
                            item {
                                ItemList(
                                    items = state.shows,
                                    viewMode = ItemListViewMode.Grid(columnCount),
                                    itemContent = { show ->
//                                        val timeText = show.attributes.nextBroadcastAt
//                                            .toLocalTime()
//                                            .format(DateTimeFormatter.ofPattern("HH:mm"))
                                        AnimeCard(
                                            show,
                                            topTitle = (show.attributes.nextBroadcastAt?.toHourMinute() ?: "—"),
                                            onClick = { onNavigateToItemDetail(show) },
                                            onStatusSelected = { newStatus ->
                                                //viewModel.updateLibraryStatus(show.id, newStatus, ItemType.Show)
                                            }
                                        )
                                    }
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}


@Composable
fun DayFilterChips(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    onDaySelected: (DayOfWeek) -> Unit
) {
    // 1️⃣ Günleri al, bugünden başlayacak şekilde sıralayacağız
    val today = Clock.System.now().toLocalDateTime(timeZone).date.dayOfWeek
    val allDays = DayOfWeek.entries.toList()

    // Bugünden başlayacak şekilde sıralama
    val orderedDays = allDays.drop(today.ordinal) + allDays.take(today.ordinal)

    var selectedDay by remember { mutableStateOf(today) }

    // 2️⃣ Yan yana kaydırılabilir Row
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        orderedDays.forEach { day ->
            FilterChip(
                selected = day == selectedDay,
                onClick = {
                    selectedDay = day
                    onDaySelected(day) // 3️⃣ ViewModel fonksiyonunu çağır
                },
                label = { Text(day.name.take(3)) }, // Örn: MON, TUE, WED
                shape = RoundedCornerShape(16.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}
