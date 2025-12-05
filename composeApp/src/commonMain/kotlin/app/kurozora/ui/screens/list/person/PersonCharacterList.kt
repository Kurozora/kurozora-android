package app.kurozora.ui.screens.list.person

import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowWidthSizeClass
import app.kurozora.ui.screens.explore.ItemType
import app.kurozora.ui.screens.list.ItemListScreen
import kurozorakit.core.KurozoraKit
import org.koin.compose.koinInject

@Composable
fun PersonCharacterListScreen(
    personId: String,
    onNavigateBack: () -> Unit,
    onNavigateToItemDetail: (Any) -> Unit,
    windowWidth: WindowWidthSizeClass,
) {
    val kit: KurozoraKit = koinInject()

    ItemListScreen(
        title = "Character",
        subtitle = "",
        itemType = ItemType.Character,
        fetcher = { nextUrl, limit ->
            var data: List<String> = emptyList()
            var next: String? = null
            kit.people().getPersonCharacters(personId, nextUrl).onSuccess { res ->
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