package app.kurozora.tracker.ui.home

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.kurozora.tracker.model.AttributesSize
import app.kurozora.tracker.ui.components.game.GamesRow
import app.kurozora.tracker.ui.components.show.FeaturedShowRow
import app.kurozora.tracker.ui.components.show.SmallLockupRow
import app.kurozora.tracker.ui.theme.Global
import app.kurozora.tracker.ui.theme.KurozoraTheme

@Composable
fun ExploreView() {
    val viewModel = HomeViewModel()
    val data = viewModel.page
    val uiState by viewModel.uiState.collectAsState()

    KurozoraTheme {
        Surface(color = Global.shared.background, modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 5.dp)
                    .scrollable(rememberScrollState(), Orientation.Horizontal),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(items = data.value) { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AttributesSize.getSize(item.attributes.size))
                            .padding(bottom = 10.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        if (item.attributes.type == "most-popular-shows") {
                            FeaturedShowRow(uiState = uiState.featuredState)
                        } else {
                            Text(text = item.attributes.title)

                            item.attributes.description?.let {
                                Text(
                                    text = it,
                                    color = Global.shared.subTextColor
                                )
                            }

                            when (item.attributes.size) {
                                "banner" -> {}
                                "large" -> {}
                                "medium" -> {}
                                "small" -> {
                                    when (item.attributes.type) {
                                        "most-popular-shows" -> {
                                            SmallLockupRow(uiState = uiState.featuredState)
                                        }
                                        "upcoming-shows" -> {
                                            SmallLockupRow(uiState = uiState.featuredState)
                                        }
                                        "new-shows" -> {
                                            SmallLockupRow(uiState = uiState.featuredState)
                                        }
                                        "shows" -> {
                                            SmallLockupRow(uiState = uiState.featuredState)
                                        }
                                        "most-popular-literatures" -> {}
                                        "upcoming-literatures" -> {}
                                        "new-literatures" -> {}
                                        "literatures" -> {}
                                        "most-popular-games" -> {
                                            GamesRow(uiState = uiState.gamesState)
                                        }
                                        "upcoming-games" -> {
                                            GamesRow(uiState = uiState.gamesState)
                                        }
                                        "new-games" -> {
                                            GamesRow(uiState = uiState.gamesState)
                                        }
                                        "games" -> {
                                            GamesRow(uiState = uiState.gamesState)
                                        }
                                        "episodes" -> {}
                                        "songs" -> {}
                                        "characters" -> {}
                                        "people" -> {}
                                        "genres" -> {}
                                        "themes" -> {}
                                        "recap" -> {}
                                        else -> {

                                        }
                                    }
                                }
                                "upcoming" -> {}
                                "video" -> {}
                                else -> {}
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(1.dp),
                            color = Global.shared.separatorColor
                        )
                    }
                }
            }
        }
    }
}
