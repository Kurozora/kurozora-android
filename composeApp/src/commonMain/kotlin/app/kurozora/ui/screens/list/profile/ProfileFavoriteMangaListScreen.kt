package app.kurozora.ui.screens.list.profile

import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowWidthSizeClass
import app.kurozora.ui.screens.explore.ItemType
import app.kurozora.ui.screens.list.ItemListScreen
import kurozorakit.core.KurozoraKit
import kurozorakit.data.enums.KKLibrary
import org.koin.compose.koinInject

@Composable
fun ProfileFavoriteMangaListScreen(
    userId: String,
    onNavigateBack: () -> Unit,
    onNavigateToItemDetail: (Any) -> Unit,
    windowWidth: WindowWidthSizeClass,
) {
    val kit: KurozoraKit = koinInject()

    ItemListScreen(
        title = "Favorite Manga",
        subtitle = "",
        itemType = ItemType.Literature,
        fetcher = { nextUrl, limit ->
            var data: List<String> = emptyList()
            var next: String? = null
            kit.auth().getUserFavorites(
                userId = userId,
                libraryKind = KKLibrary.Kind.LITERATURES,
                next = nextUrl,
            ).onSuccess { res ->
                data = res.data.literatures?.map { it.id } ?: emptyList()
                next = res.next
            }
            data to next
        },
        windowWidth = windowWidth,
        onNavigateBack = onNavigateBack,
        onNavigateToItemDetail = onNavigateToItemDetail
    )
}
