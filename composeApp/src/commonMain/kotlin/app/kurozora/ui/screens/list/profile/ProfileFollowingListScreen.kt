package app.kurozora.ui.screens.list.profile

import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowWidthSizeClass
import app.kurozora.ui.screens.explore.ItemType
import app.kurozora.ui.screens.list.ItemListScreen
import kurozorakit.core.KurozoraKit
import kurozorakit.data.enums.UsersListType
import org.koin.compose.koinInject

@Composable
fun ProfileFollowingListScreen(
    userId: String,
    onNavigateBack: () -> Unit,
    onNavigateToItemDetail: (Any) -> Unit,
    windowWidth: WindowWidthSizeClass,
) {
    val kit: KurozoraKit = koinInject()

    ItemListScreen(
        title = "Followings",
        subtitle = "",
        itemType = ItemType.User,
        fetcher = { nextUrl, limit ->
            var data: List<String> = emptyList()
            var next: String? = null
            kit.auth().getFollowList(
                userId = userId,
                followList = UsersListType.following,
                next = nextUrl,
            ).onSuccess { res ->
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
