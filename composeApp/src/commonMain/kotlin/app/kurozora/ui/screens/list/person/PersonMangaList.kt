package app.kurozora.ui.screens.list.person

import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowWidthSizeClass
import app.kurozora.ui.screens.explore.ItemType
import app.kurozora.ui.screens.list.ItemListScreen
import kurozorakit.core.KurozoraKit
import org.koin.compose.koinInject

@Composable
fun PersonMangaListScreen(
    personId: String,
    onNavigateBack: () -> Unit,
    onNavigateToItemDetail: (Any) -> Unit,
    windowWidth: WindowWidthSizeClass,
) {
    val kit: KurozoraKit = koinInject()

    ItemListScreen(
        title = "Manga",
        subtitle = "",
        itemType = ItemType.Literature,
        fetcher = { nextUrl, limit ->
            var data: List<String> = emptyList()
            var next: String? = null
            kit.people().getPersonLiteratures(personId, nextUrl).onSuccess { res ->
                data = res.data.map { it.id }
                next = res.next
            }
            data to next
        },
        windowWidth = windowWidth,
        onNavigateBack = onNavigateBack,
        onNavigateToItemDetail = onNavigateToItemDetail
    )
}