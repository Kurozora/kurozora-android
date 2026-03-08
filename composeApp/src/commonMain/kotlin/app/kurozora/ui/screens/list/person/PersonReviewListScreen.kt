package app.kurozora.ui.screens.list.person

import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowWidthSizeClass
import app.kurozora.ui.screens.explore.ItemType
import app.kurozora.ui.screens.list.ItemListScreen
import app.kurozora.ui.screens.list.ItemListViewModel
import kurozorakit.core.KurozoraKit
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PersonReviewListScreen(
    personId: String,
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
            val map: MutableMap<String, Any> = mutableMapOf()

            kit.people().getPersonReviews(personId, nextUrl).onSuccess { res ->
                map.putAll(res.data.associateBy { it.id })
                data = map.keys.toList()
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
