package com.seloreis.kurozora.ui.screens.list.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.window.core.layout.WindowWidthSizeClass
import com.seloreis.kurozora.ui.screens.explore.ItemType
import com.seloreis.kurozora.ui.screens.list.ItemListScreen
import kotlinx.coroutines.launch
import kurozorakit.core.KurozoraKit
import kurozorakit.data.enums.KKLibrary
import kurozorakit.data.models.show.Show
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

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