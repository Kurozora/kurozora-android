package com.seloreis.kurozora.ui.screens.list.literature

import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowWidthSizeClass
import com.seloreis.kurozora.ui.screens.explore.ItemType
import com.seloreis.kurozora.ui.screens.list.ItemListScreen
import kurozorakit.core.KurozoraKit
import org.koin.compose.koinInject

@Composable
fun LiteratureRelatedGameListScreen(
    litId: String,
    onNavigateBack: () -> Unit,
    onNavigateToItemDetail: (Any) -> Unit,
    windowWidth: WindowWidthSizeClass,
) {
    val kit: KurozoraKit = koinInject()

    ItemListScreen(
        title = "Related Game",
        subtitle = "",
        itemType = ItemType.Game,
        fetcher = { nextUrl, limit ->
            var data: List<String> = emptyList()
            var next: String? = null
            kit.literature().getRelatedGames(litId,nextUrl).onSuccess { res ->
                data = res.data.map { it.game.id }
                next = res.next
            }
            data to next
        },
        windowWidth = windowWidth,
        onNavigateBack = onNavigateBack,
        onNavigateToItemDetail = onNavigateToItemDetail
    )
}