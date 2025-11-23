package app.kurozora.ui.screens.list

data class ItemListState(
    val itemIds: List<String> = emptyList(),
    val items: Map<String, Any> = emptyMap(),
    val next: String? = null,
    val loadingItems: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
)
