package com.seloreis.kurozora.ui.screens.feed

import kurozorakit.data.models.feed.message.FeedMessage

data class FeedState(
    val isLoading: Boolean = false,
    val feedMessages: List<FeedMessage> = emptyList(),
    val error: String? = null,
    val nextPageUrl: String? = null
)