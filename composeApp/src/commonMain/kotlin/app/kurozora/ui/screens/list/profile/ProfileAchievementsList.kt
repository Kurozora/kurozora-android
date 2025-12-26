package app.kurozora.ui.screens.list.profile

import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowWidthSizeClass
import app.kurozora.ui.screens.explore.ItemType
import app.kurozora.ui.screens.list.ItemListScreen
import kurozorakit.core.KurozoraKit
import kurozorakit.data.models.user.User
import org.koin.compose.koinInject

@Composable
fun ProfileAchievementsListScreen(
    userId: String,
    onNavigateBack: () -> Unit,
    onNavigateToItemDetail: (Any) -> Unit,
    windowWidth: WindowWidthSizeClass,
) {
    val kit: KurozoraKit = koinInject()

    ItemListScreen(
        title = "Achievements",
        subtitle = "",
        itemType = ItemType.Review,
        fetcher = { nextUrl, limit ->
            var data: List<String> = emptyList()
            var next: String? = null
            kit.auth().getUserReviews(
                userId = userId,
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

//fun User.getAchievements(): List<String> {
//    return this.let { user ->
//        val isStaff = user.attributes.isStaff
//        val isPro = user.attributes.isPro
//        val isVerified = user.attributes.isVerified
//        val isDeveloper = user.attributes.isDeveloper
//        val isSubscribed = user.attributes.isSubscribed
//        val isEarlySupporter = user.attributes.isEarlySupporter
//        listOf()
//    }
//}