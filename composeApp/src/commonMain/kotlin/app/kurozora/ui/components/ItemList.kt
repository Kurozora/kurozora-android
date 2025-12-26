package app.kurozora.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.kurozora.ui.screens.explore.ItemType
import kurozora.composeapp.generated.resources.Res
import kurozorakit.data.models.game.Game
import kurozorakit.data.models.literature.Literature
import kurozorakit.data.models.show.Show

sealed class ItemListViewMode {
    data class Grid(val columns: Int = 2) : ItemListViewMode()
    object Horizontal : ItemListViewMode()
    object Vertical : ItemListViewMode()
}

@Suppress("FrequentlyChangingValue")
@Composable
fun <T> ItemList(
    items: List<T>,
    itemType: ItemType? = null,
    viewMode: ItemListViewMode,
    modifier: Modifier = Modifier,
    itemSpacing: Dp = 12.dp,
    contentPadding: PaddingValues = PaddingValues(8.dp),
    itemContent: @Composable (T) -> Unit,
    onLoadMore: (() -> Unit)? = null,
    isLoadingMore: Boolean = false,
    emptyContent: @Composable () -> Unit = {
        Box(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            contentAlignment = Alignment.Center
        ) { Text("No items found.") }
    },
) {
    var emptyAnimeLibrary: ByteArray? by remember { mutableStateOf(null) }
    var emptyMangaLibrary: ByteArray? by remember { mutableStateOf(null) }
    var emptyGameLibrary: ByteArray? by remember { mutableStateOf(null) }
    var emptyImage: ImageBitmap? = null

    LaunchedEffect(Unit) {
        emptyAnimeLibrary = Res.readBytes("files/static/placeholders/empty_anime_library.webp")
        emptyMangaLibrary = Res.readBytes("files/static/placeholders/empty_manga_library.webp")
        emptyGameLibrary = Res.readBytes("files/static/placeholders/empty_game_library.webp")
        emptyImage = when (itemType) {
            ItemType.Show -> emptyAnimeLibrary?.decodeToImageBitmap()
            ItemType.Literature -> emptyMangaLibrary?.decodeToImageBitmap()
            ItemType.Game -> emptyGameLibrary?.decodeToImageBitmap()
            else -> null
        }
    }


    if (items.isEmpty()) {
        if (emptyImage != null) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = emptyImage!!,
                    contentDescription = "empty",
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                )
            }
        } else {
            emptyContent()
        }
        return
    }


    when (viewMode) {
        ItemListViewMode.Horizontal -> {
            val listState = rememberLazyListState()
            LaunchedEffect(listState.firstVisibleItemIndex, listState.layoutInfo.totalItemsCount) {
                if (onLoadMore != null && listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == items.lastIndex) {
                    onLoadMore()
                }
            }

            LazyRow(
                state = listState,
                modifier = modifier,
                contentPadding = contentPadding,
                horizontalArrangement = Arrangement.spacedBy(itemSpacing)
            ) {
                items(items) { item -> Box { itemContent(item) } }
                if (isLoadingMore) {
                    item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
                }
                if (onLoadMore != null) {
                    item {
                        Button(onClick = onLoadMore, modifier = Modifier.padding(16.dp)) {
                            Text("Load More")
                        }
                    }
                }
            }
        }

        ItemListViewMode.Vertical -> {
            val listState = rememberLazyListState()
            LaunchedEffect(listState.firstVisibleItemIndex, listState.layoutInfo.totalItemsCount) {
                if (onLoadMore != null && listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == items.lastIndex) {
                    onLoadMore()
                }
            }

            LazyColumn(
                state = listState,
                modifier = modifier,
                contentPadding = contentPadding,
                verticalArrangement = Arrangement.spacedBy(itemSpacing)
            ) {
                items(items) { item -> Box { itemContent(item) } }
                if (isLoadingMore) {
                    item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
                }
                if (onLoadMore != null) {
                    item {
                        Button(onClick = onLoadMore, modifier = Modifier.padding(16.dp)) {
                            Text("Load More")
                        }
                    }
                }
            }
        }

        is ItemListViewMode.Grid -> {
            val listState = rememberLazyGridState()
            LaunchedEffect(listState.firstVisibleItemIndex, listState.layoutInfo.totalItemsCount) {
                if (onLoadMore != null && listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == items.lastIndex) {
                    onLoadMore()
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(viewMode.columns),
                state = listState,
                modifier = modifier.heightIn(max = 800.dp),
                contentPadding = contentPadding,
                verticalArrangement = Arrangement.spacedBy(itemSpacing),
                horizontalArrangement = Arrangement.spacedBy(itemSpacing)
            ) {
                items(items) { item -> Box { itemContent(item) } }

                if (isLoadingMore) {
                    item(span = { GridItemSpan(viewMode.columns) }) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }

                if (onLoadMore != null) {
                    item(span = { GridItemSpan(viewMode.columns) }) {
                        Button(
                            onClick = onLoadMore,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text("Load More")
                        }
                    }
                }
            }
        }
    }
}
