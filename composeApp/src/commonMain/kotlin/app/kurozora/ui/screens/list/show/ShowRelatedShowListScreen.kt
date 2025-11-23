package app.kurozora.ui.screens.list.show

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.window.core.layout.WindowWidthSizeClass
import app.kurozora.ui.screens.explore.ItemType
import app.kurozora.ui.screens.list.ItemListScreen
import kotlinx.coroutines.launch
import kurozorakit.core.KurozoraKit
import kurozorakit.data.models.show.Show
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ShowRelatedShowListScreen(
    showId: String,
    onNavigateBack: () -> Unit,
    onNavigateToItemDetail: (Any) -> Unit,
    windowWidth: WindowWidthSizeClass,
    viewModel: RelatedShowsViewModel = koinViewModel(),
) {
    val kit: KurozoraKit = koinInject()
    val relatedShows by viewModel.relatedShows

    ItemListScreen(
        title = "Related Show",
        subtitle = "",
        itemType = ItemType.Show,
        preloadedItems = relatedShows,
        fetcher = { nextUrl, limit ->
            var data: List<String> = emptyList()
            var next: String? = null
            kit.show().getRelatedShows(showId, nextUrl).onSuccess { res ->
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

class RelatedShowsViewModel : ViewModel() {
    // Map: itemId -> Show
    private val _relatedShows = mutableStateOf<Map<String, Show>>(emptyMap())
    val relatedShows get() = _relatedShows

    // Veriyi set etme
    fun setRelatedShows(shows: Map<String, Show>) {
        _relatedShows.value = shows
    }

    // Eğer lazım olursa fetcher ile load etme
    fun loadRelatedShows(showId: String, kit: KurozoraKit) {
        viewModelScope.launch {
            kit.show().getRelatedShows(showId).onSuccess { res ->
                val map = res.data.associate { it.show.id to it.show }
                _relatedShows.value = map
            }
        }
    }
}
