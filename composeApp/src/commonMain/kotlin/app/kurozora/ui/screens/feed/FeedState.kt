package app.kurozora.ui.screens.feed

import kurozorakit.data.models.feed.message.FeedMessage

sealed interface FeedUIState {
    object Loading : FeedUIState
    object Composing : FeedUIState
    data class Success(
        val feedMessages: List<FeedMessage>,
        val nextPageUrl: String?
    ) : FeedUIState
    data class Error(val message: String) : FeedUIState
    data class Replying(
        val message: FeedMessage,
        val feedMessages: List<FeedMessage>,
        val nextPageUrl: String?
    ) : FeedUIState
    data class Editing(
        val message: FeedMessage,
        val feedMessages: List<FeedMessage>,
        val nextPageUrl: String?
    ) : FeedUIState
}

data class FeedState(
    val uiState: FeedUIState = FeedUIState.Loading,
    val showNewMessageDialog: Boolean = false,
    val newMessageContent: String = "",
    val replyContent: String = "",
    val editContent: String = "",
    val isNSFW: Boolean = false,
    val isSpoiler: Boolean = false,
    val selectedMessageForAction: FeedMessage? = null,
    val showDeleteDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val showReplyDialog: Boolean = false,
    val isLoadingMore: Boolean = false,
    val postingMessage: Boolean = false,
    // replies
    val showMessageDetailDialog: Boolean = false,
    val selectedMessageForDetail: FeedMessage? = null,
    val messageReplies: List<FeedMessage> = emptyList(),
    val isLoadingReplies: Boolean = false,
    val repliesError: String? = null,
)