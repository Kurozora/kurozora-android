package app.kurozora.ui.screens.list.show

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.window.core.layout.WindowWidthSizeClass
import app.kurozora.ui.screens.explore.ItemType
import app.kurozora.ui.screens.list.ItemListScreen
import app.kurozora.ui.screens.list.ItemListViewModel
import kotlinx.coroutines.launch
import kurozorakit.core.KurozoraKit
import kurozorakit.data.models.show.Show
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun ShowRelatedShowListScreen(
    showId: String,
    onNavigateBack: () -> Unit,
    onNavigateToItemDetail: (Any) -> Unit,
    windowWidth: WindowWidthSizeClass,
    viewModel: ItemListViewModel = koinViewModel(),
) {
    val kit: KurozoraKit = koinInject()

    ItemListScreen(
        title = "Related Show",
        subtitle = "",
        itemType = ItemType.RelatedShow,
        preloadedItems = null,
        fetcher = { nextUrl, limit ->
            var data: List<String> = emptyList()
            var next: String? = null
            val map: Map<String, Any> = mutableMapOf()

            kit.show().getRelatedShows(showId, nextUrl).onSuccess { res ->
                val m = res.data.associateBy { it.id.toString() }
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
