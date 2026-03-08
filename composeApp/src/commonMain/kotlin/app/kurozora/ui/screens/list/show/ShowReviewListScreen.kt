package app.kurozora.ui.screens.list.show

import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowWidthSizeClass
import app.kurozora.ui.screens.explore.ItemType
import app.kurozora.ui.screens.list.ItemListScreen
import app.kurozora.ui.screens.list.ItemListViewModel
import kurozorakit.core.KurozoraKit
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ShowReviewListScreen(
    showId: String,
    onNavigateBack: () -> Unit,
    onNavigateToItemDetail: (Any) -> Unit,
    windowWidth: WindowWidthSizeClass,
    viewModel: ItemListViewModel = koinViewModel(),
) {
    val kit: KurozoraKit = koinInject()

    ItemListScreen(
        title = "Reviews",
        itemType = ItemType.Review,
        preloadedItems = null,
        fetcher = { nextUrl, limit ->
            var data: List<String> = emptyList()
            var next: String? = null
            val map: Map<String, Any> = mutableMapOf()

            kit.show().getShowReviews(showId, nextUrl).onSuccess { res ->
                val m = res.data.associateBy { it.id } // id -> Review
                (map as MutableMap).putAll(m)
                data = m.keys.toList()
                next = res.next
            }

            viewModel.setPreloadedItems(map)
            data to next
        },
        windowWidth = windowWidth,
        onNavigateBack = onNavigateBack,
        onNavigateToItemDetail = onNavigateToItemDetail
    )
}