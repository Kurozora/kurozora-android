package com.seloreis.kurozora.ui.screens.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seloreis.kurozora.ui.components.cards.FeedMessageCard
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FeedScreen(
    viewModel: FeedViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    when {
        state.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        state.error != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Error: ${state.error}")
            }
        }
        else -> {
            LazyColumn(
                contentPadding = PaddingValues(8.dp)
            ) {
                items(state.feedMessages) { message ->
                    FeedMessageCard(
                        feedMessage = message,
                        onReplyClick = {},
                        onReshareClick = { viewModel.reshareMessage(it) },
                        onHeartClick = { viewModel.heartMessage(it) },
                        onMoreClick = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                // Load More
                item {
                    if (state.nextPageUrl != null) {
                        Button(
                            onClick = { viewModel.loadMore() },
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        ) {
                            Text("Load More")
                        }
                    }
                }
            }
        }
    }
}