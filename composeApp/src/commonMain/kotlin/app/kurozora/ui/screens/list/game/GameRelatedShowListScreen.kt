package app.kurozora.ui.screens.list.game

import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowWidthSizeClass
import app.kurozora.ui.screens.explore.ItemType
import app.kurozora.ui.screens.list.ItemListScreen
import kurozorakit.core.KurozoraKit
import org.koin.compose.koinInject

@Composable
fun GameRelatedShowListScreen(
    gameId: String,
    onNavigateBack: () -> Unit,
    onNavigateToItemDetail: (Any) -> Unit,
    windowWidth: WindowWidthSizeClass,
) {
    val kit: KurozoraKit = koinInject()

    ItemListScreen(
        title = "Related Show",
        subtitle = "",
        itemType = ItemType.Show,
        //preloadedItems = relatedShows,
        fetcher = { nextUrl, limit ->
            var data: List<String> = emptyList()
            var next: String? = null
            kit.game().getRelatedShows(gameId, nextUrl).onSuccess { res ->
                data = res.data.map { it.show.id }
                next = res.next
            }
            data to next
        },
        windowWidth = windowWidth,
        onNavigateBack = onNavigateBack,
        onNavigateToItemDetail = onNavigateToItemDetail
    )
}
