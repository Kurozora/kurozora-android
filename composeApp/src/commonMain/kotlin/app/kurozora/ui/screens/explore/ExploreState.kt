package app.kurozora.ui.screens.explore

import kurozorakit.data.models.explore.ExploreCategory
import kurozorakit.data.models.genre.Genre
import kurozorakit.data.models.theme.Theme

data class ExploreState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val categories: List<ExploreCategory> = emptyList(),
    val categoryItemIdentities: Map<String, List<String>> = emptyMap(),
    val categoryItems: Map<String, Map<String, Any>> = emptyMap(), // categoryId -> itemId -> item
    val loadingItems: Set<String> = emptySet(),
    val genre: Genre? = null,
    val theme: Theme? = null,
)
