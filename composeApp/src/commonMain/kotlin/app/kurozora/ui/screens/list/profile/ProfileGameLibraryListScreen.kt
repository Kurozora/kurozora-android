package app.kurozora.ui.screens.list.profile

import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowWidthSizeClass
import app.kurozora.ui.screens.explore.ItemType
import app.kurozora.ui.screens.list.ItemListScreen
import kurozorakit.core.KurozoraKit
import kurozorakit.data.enums.KKLibrary
import org.koin.compose.koinInject

@Composable
fun ProfileGameLibraryListScreen(
    userId: String,
    onNavigateBack: () -> Unit,
    onNavigateToItemDetail: (Any) -> Unit,
    windowWidth: WindowWidthSizeClass,
) {
    val kit: KurozoraKit = koinInject()

    ItemListScreen(
        title = "Game Library",
        subtitle = "",
        itemType = ItemType.Game,
        fetcher = { nextUrl, limit ->
            var data: List<String> = emptyList()
            var next: String? = null
            kit.auth().getUserLibrary(
                userId = userId,
                libraryKind = KKLibrary.Kind.GAMES,
                libraryStatus = KKLibrary.Status.INPROGRESS,
                next = nextUrl,
            ).onSuccess { res ->
                data = res.data.games?.map { it.id } ?: emptyList()
                next = res.next
            }
            data to next
        },
        windowWidth = windowWidth,
        onNavigateBack = onNavigateBack,
        onNavigateToItemDetail = onNavigateToItemDetail
    )
}
