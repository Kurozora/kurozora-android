package app.kurozora.ui.screens.reminder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import app.kurozora.ui.components.ItemList
import app.kurozora.ui.components.ItemListViewMode
import app.kurozora.ui.components.cards.AnimeCard
import app.kurozora.ui.components.cards.GameCard
import app.kurozora.ui.components.cards.LiteratureCard
import app.kurozora.ui.screens.library.LibraryTab
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    windowWidth: WindowWidthSizeClass,
    onNavigateToItemDetail: (Any) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ReminderViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Reminders",
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
            // Üst sekmeler
//            TabRow(selectedTabIndex = state.selectedTab.ordinal) {
//                LibraryTab.entries.forEachIndexed { index, tab ->
//                    Tab(
//                        selected = index == state.selectedTab.ordinal,
//                        onClick = { viewModel.selectTab(tab) },
//                        text = { Text(tab.name) }
//                    )
//                }
//            }

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

                        when (state.selectedTab) {
                            LibraryTab.Animes -> {
                                if (state.shows.isNotEmpty()) {
                                    item {
                                        ItemList(
                                            items = state.shows,
                                            viewMode = ItemListViewMode.Grid(columnCount),
                                            itemContent = { show ->
                                                AnimeCard(
                                                    show,
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

                            LibraryTab.Mangas -> {
                                if (state.literatures.isNotEmpty()) {
                                    item {
                                        ItemList(
                                            items = state.literatures,
                                            viewMode = ItemListViewMode.Grid(columnCount),
                                            itemContent = { lit ->
                                                LiteratureCard(
                                                    lit,
                                                    onClick = { onNavigateToItemDetail(lit) },
                                                    onStatusSelected = { newStatus ->
                                                        //viewModel.updateLibraryStatus(lit.id, newStatus, ItemType.Literature)
                                                    }
                                                )
                                            }
                                        )
                                    }
                                }
                            }

                            LibraryTab.Games -> {
                                if (state.games.isNotEmpty()) {
                                    item {
                                        ItemList(
                                            items = state.games,
                                            viewMode = ItemListViewMode.Grid(columnCount),
                                            itemContent = { game ->
                                                GameCard(
                                                    game,
                                                    onClick = { onNavigateToItemDetail(game) },
                                                    onStatusSelected = { newStatus ->
                                                        //viewModel.updateLibraryStatus(game.id, newStatus, ItemType.Game)
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
    }
}
