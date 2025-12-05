package app.kurozora.ui.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kurozorakit.core.KurozoraKit
import kurozorakit.data.models.feed.message.FeedMessage
import kurozorakit.data.models.feed.message.FeedMessageIdentity
import kurozorakit.data.models.feed.message.FeedMessageRequest
import kurozorakit.data.models.feed.message.update.FeedMessageUpdate
import kurozorakit.shared.Result

class FeedViewModel(
    private val kurozoraKit: KurozoraKit,
) : ViewModel() {
    private val _state = MutableStateFlow(FeedState())
    val state: StateFlow<FeedState> = _state.asStateFlow()

    init {
        loadExploreFeed()
    }

    fun loadExploreFeed() {
        viewModelScope.launch {
            _state.value = FeedState(uiState = FeedUIState.Loading)
            when (val result = kurozoraKit.feed().getExploreFeed()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        uiState = FeedUIState.Success(
                            feedMessages = result.data.data,
                            nextPageUrl = result.data.next
                        )
                    )
                }

                is Result.Error -> {
                    _state.value = _state.value.copy(
                        uiState = FeedUIState.Error(result.error.message ?: "Unknown error")
                    )
                }
            }
        }
    }

    fun loadMore() {
        val currentState = _state.value
        val currentUIState = currentState.uiState
        val nextPageUrl = when (currentUIState) {
            is FeedUIState.Success -> currentUIState.nextPageUrl
            is FeedUIState.Replying -> currentUIState.nextPageUrl
            is FeedUIState.Editing -> currentUIState.nextPageUrl
            else -> null
        }

        if (nextPageUrl == null) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingMore = true)

            when (val result = kurozoraKit.feed().getExploreFeed(next = nextPageUrl)) {
                is Result.Success -> {
                    val currentMessages = when (val uiState = _state.value.uiState) {
                        is FeedUIState.Success -> uiState.feedMessages
                        is FeedUIState.Replying -> uiState.feedMessages
                        is FeedUIState.Editing -> uiState.feedMessages
                        else -> emptyList()
                    }

                    val newMessages = currentMessages + result.data.data

                    when (_state.value.uiState) {
                        is FeedUIState.Success -> {
                            _state.value = _state.value.copy(
                                uiState = FeedUIState.Success(
                                    feedMessages = newMessages,
                                    nextPageUrl = result.data.next
                                ),
                                isLoadingMore = false
                            )
                        }
                        is FeedUIState.Replying -> {
                            val uiState = _state.value.uiState as FeedUIState.Replying
                            _state.value = _state.value.copy(
                                uiState = FeedUIState.Replying(
                                    message = uiState.message,
                                    feedMessages = newMessages,
                                    nextPageUrl = result.data.next
                                ),
                                isLoadingMore = false
                            )
                        }
                        is FeedUIState.Editing -> {
                            val uiState = _state.value.uiState as FeedUIState.Editing
                            _state.value = _state.value.copy(
                                uiState = FeedUIState.Editing(
                                    message = uiState.message,
                                    feedMessages = newMessages,
                                    nextPageUrl = result.data.next
                                ),
                                isLoadingMore = false
                            )
                        }
                        else -> {}
                    }
                }

                is Result.Error -> {
                    _state.value = _state.value.copy(
                        isLoadingMore = false
                    )
                    // Handle error appropriately
                }
            }
        }
    }

    fun postMessage() {
        val content = _state.value.newMessageContent.trim()
        if (content.isEmpty() || _state.value.postingMessage) return

        viewModelScope.launch {
            _state.value = _state.value.copy(postingMessage = true)

            val request = FeedMessageRequest(
                content = content,
                isNSFW = _state.value.isNSFW,
                isSpoiler = _state.value.isSpoiler
            )

            when (val result = kurozoraKit.feed().postFeedMessage(request)) {
                is Result.Success -> {
                    // Add new message to the beginning of the list
                    val newMessage = result.data.data.firstOrNull()
                    val currentMessages = when (val uiState = _state.value.uiState) {
                        is FeedUIState.Success -> uiState.feedMessages
                        else -> emptyList()
                    }

                    val updatedMessages = if (newMessage != null) {
                        listOf(newMessage) + currentMessages
                    } else {
                        currentMessages
                    }

                    _state.value = FeedState(
                        uiState = FeedUIState.Success(
                            feedMessages = updatedMessages,
                            nextPageUrl = when (val uiState = _state.value.uiState) {
                                is FeedUIState.Success -> uiState.nextPageUrl
                                else -> null
                            }
                        )
                    )
                }

                is Result.Error -> {
                    _state.value = _state.value.copy(
                        uiState = FeedUIState.Error(result.error.message ?: "Failed to post message"),
                        postingMessage = false
                    )
                }
            }
        }
    }

    fun replyToMessage(message: FeedMessage) {
        val content = _state.value.replyContent.trim()
        if (content.isEmpty() || _state.value.postingMessage) return

        viewModelScope.launch {
            _state.value = _state.value.copy(postingMessage = true)

            val request = FeedMessageRequest(
                content = content,
                parentIdentity = message.id,
                isReply = true,
                isNSFW = _state.value.isNSFW,
                isSpoiler = _state.value.isSpoiler,
                isReShare = false
            )

            when (val result = kurozoraKit.feed().postFeedMessage(request)) {
                is Result.Success -> {
                    // Update reply count
                    updateLocalMessage(message.id) { attributes ->
                        attributes.metrics.replyCount++
                    }

                    // Close reply dialog
                    _state.value = _state.value.copy(
                        showReplyDialog = false,
                        replyContent = "",
                        postingMessage = false
                    )
                }

                is Result.Error -> {
                    _state.value = _state.value.copy(
                        postingMessage = false
                    )
                }
            }
        }
    }

    fun editMessage(message: FeedMessage) {
        val content = _state.value.editContent.trim()
        if (content.isEmpty() || _state.value.postingMessage) return

        viewModelScope.launch {
            _state.value = _state.value.copy(postingMessage = true)

            val request = FeedMessageRequest(
                content = content,
                isNSFW = _state.value.isNSFW,
                isSpoiler = _state.value.isSpoiler
            )

            when (val result = kurozoraKit.feed().updateFeedMessage(message.id, request)) {
                is Result.Success -> {
                    // Update local message
                    updateLocalMessage(message.id) { attributes ->
                        attributes.content = content
                        attributes.contentHTML = result.data.data.contentHTML ?: ""
                        attributes.contentMarkdown = result.data.data.contentMarkdown ?: ""
                        attributes.isNSFW = _state.value.isNSFW
                        attributes.isSpoiler = _state.value.isSpoiler
                    }

                    // Close edit dialog
                    _state.value = _state.value.copy(
                        showEditDialog = false,
                        editContent = "",
                        postingMessage = false
                    )
                }

                is Result.Error -> {
                    _state.value = _state.value.copy(
                        postingMessage = false
                    )
                }
            }
        }
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            when (val result = kurozoraKit.feed().deleteFeedMessage(messageId)) {
                is Result.Success -> {
                    // Remove message from list
                    val currentMessages = when (val uiState = _state.value.uiState) {
                        is FeedUIState.Success -> uiState.feedMessages
                        else -> emptyList()
                    }

                    val updatedMessages = currentMessages.filter { it.id != messageId }

                    _state.value = _state.value.copy(
                        uiState = FeedUIState.Success(
                            feedMessages = updatedMessages,
                            nextPageUrl = when (val uiState = _state.value.uiState) {
                                is FeedUIState.Success -> uiState.nextPageUrl
                                else -> null
                            }
                        ),
                        showDeleteDialog = false,
                        selectedMessageForAction = null
                    )
                }

                is Result.Error -> {
                    // Handle error
                }
            }
        }
    }

    fun heartMessage(message: FeedMessage) {
        viewModelScope.launch {
            val isCurrentlyHearted = message.attributes.isHearted ?: false
            val update = FeedMessageUpdate(isHearted = !isCurrentlyHearted)

            when (val result = kurozoraKit.feed().heartFeedMessage(message.id)) {
                is Result.Success -> {
                    updateLocalMessage(message.id) { attributes ->
                        attributes.isHearted = !isCurrentlyHearted
                        if (!isCurrentlyHearted) {
                            attributes.metrics.heartCount++
                        } else {
                            attributes.metrics.heartCount--
                        }
                    }
                }

                is Result.Error -> {
                    // Handle error
                }
            }
        }
    }

    fun reshareMessage(message: FeedMessage) {
        viewModelScope.launch {
            val isCurrentlyPinned = message.attributes.isPinned

            if (isCurrentlyPinned) {
                // Unreshare
                val update = FeedMessageUpdate(isPinned = false)
                when (val result = kurozoraKit.feed().pinFeedMessage(message.id)) {
                    is Result.Success -> {
                        updateLocalMessage(message.id) { attributes ->
                            attributes.isPinned = false
                            attributes.metrics.reShareCount--
                        }
                    }

                    is Result.Error -> {
                        // Handle error
                    }
                }
            } else {
                // Reshare
                val request = FeedMessageRequest(
                    content = message.attributes.content,
                    parentIdentity = message.id,
                    isReShare = true,
                    isReply = false,
                    isNSFW = message.attributes.isNSFW,
                    isSpoiler = message.attributes.isSpoiler
                )

                when (val result = kurozoraKit.feed().postFeedMessage(request)) {
                    is Result.Success -> {
                        updateLocalMessage(message.id) { attributes ->
                            attributes.metrics.reShareCount++
                        }

                        // Add reshare to feed
                        val currentMessages = when (val uiState = _state.value.uiState) {
                            is FeedUIState.Success -> uiState.feedMessages
                            else -> emptyList()
                        }

                        val newMessage = result.data.data.firstOrNull()
                        val updatedMessages = if (newMessage != null) {
                            listOf(newMessage) + currentMessages
                        } else {
                            currentMessages
                        }

                        _state.value = _state.value.copy(
                            uiState = FeedUIState.Success(
                                feedMessages = updatedMessages,
                                nextPageUrl = when (val uiState = _state.value.uiState) {
                                    is FeedUIState.Success -> uiState.nextPageUrl
                                    else -> null
                                }
                            )
                        )
                    }

                    is Result.Error -> {
                        // Handle error
                    }
                }
            }
        }
    }

    fun loadMessageReplies(messageId: String) {
        viewModelScope.launch {
            try {
                when (val result = kurozoraKit.feed().getFeedMessageReplies(messageId)) {
                    is Result.Success -> {
                        _state.value = _state.value.copy(
                            messageReplies = result.data.data,
                            isLoadingReplies = false
                        )
                    }
                    is Result.Error -> {
                        _state.value = _state.value.copy(
                            repliesError = result.error.message ?: "Failed to load replies",
                            isLoadingReplies = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    repliesError = e.message ?: "Unknown error",
                    isLoadingReplies = false
                )
            }
        }
    }

    fun postReplyToSelectedMessage(content: String) {
        val selectedMessage = _state.value.selectedMessageForDetail
        if (selectedMessage == null || content.trim().isEmpty() || _state.value.postingMessage) return

        viewModelScope.launch {
            _state.value = _state.value.copy(postingMessage = true)

            val request = FeedMessageRequest(
                content = content,
                parentIdentity = selectedMessage.id,
                isReply = true,
                isNSFW = _state.value.isNSFW,
                isSpoiler = _state.value.isSpoiler,
                isReShare = false
            )

            when (val result = kurozoraKit.feed().postFeedMessage(request)) {
                is Result.Success -> {
                    // Yeni reply'ı listeye ekle
                    val newReply = result.data.data.firstOrNull()
                    if (newReply != null) {
                        _state.value = _state.value.copy(
                            messageReplies = listOf(newReply) + _state.value.messageReplies,
                            postingMessage = false
                        )

                        // Ana mesajın reply count'unu güncelle
                        updateLocalMessage(selectedMessage.id) { attributes ->
                            attributes.metrics.replyCount++
                        }
                    } else {
                        _state.value = _state.value.copy(postingMessage = false)
                    }
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(
                        postingMessage = false,
                        repliesError = result.error.message ?: "Failed to post reply"
                    )
                }
            }
        }
    }


    fun updateNewMessageContent(content: String) {
        _state.value = _state.value.copy(newMessageContent = content)
    }

    fun updateReplyContent(content: String) {
        _state.value = _state.value.copy(replyContent = content)
    }

    fun updateEditContent(content: String) {
        _state.value = _state.value.copy(editContent = content)
    }

    fun toggleNSFW() {
        _state.value = _state.value.copy(isNSFW = !_state.value.isNSFW)
    }

    fun toggleSpoiler() {
        _state.value = _state.value.copy(isSpoiler = !_state.value.isSpoiler)
    }

    fun showNewMessageDialog() {
        _state.value = _state.value.copy(
            showNewMessageDialog = true,
            newMessageContent = ""
        )
    }

    fun hideNewMessageDialog() {
        _state.value = _state.value.copy(
            showNewMessageDialog = false,
            newMessageContent = ""
        )
    }

    fun showMessageDetail(message: FeedMessage) {
        _state.value = _state.value.copy(
            showMessageDetailDialog = true,
            selectedMessageForDetail = message,
            isLoadingReplies = true,
            messageReplies = emptyList(),
            repliesError = null
        )

        // Replies'ları yükle
        loadMessageReplies(message.id)
    }

    fun hideMessageDetail() {
        _state.value = _state.value.copy(
            showMessageDetailDialog = false,
            selectedMessageForDetail = null,
            messageReplies = emptyList(),
            repliesError = null
        )
    }

    fun showDeleteDialog(message: FeedMessage) {
        _state.value = _state.value.copy(
            selectedMessageForAction = message,
            showDeleteDialog = true
        )
    }

    fun hideDeleteDialog() {
        _state.value = _state.value.copy(
            showDeleteDialog = false,
            selectedMessageForAction = null
        )
    }

    fun showEditDialog(message: FeedMessage) {
        _state.value = _state.value.copy(
            selectedMessageForAction = message,
            showEditDialog = true,
            editContent = message.attributes.content
        )
    }

    fun hideEditDialog() {
        _state.value = _state.value.copy(
            showEditDialog = false,
            selectedMessageForAction = null,
            editContent = ""
        )
    }

    fun showReplyDialog(message: FeedMessage) {
        _state.value = _state.value.copy(
            selectedMessageForAction = message,
            showReplyDialog = true,
            replyContent = ""
        )
    }

    fun hideReplyDialog() {
        _state.value = _state.value.copy(
            showReplyDialog = false,
            selectedMessageForAction = null,
            replyContent = ""
        )
    }

    fun retry() {
        loadExploreFeed()
    }

    private fun updateLocalMessage(messageId: String, update: (FeedMessage.Attributes) -> Unit) {
        when (val currentUIState = _state.value.uiState) {
            is FeedUIState.Success -> {
                val updatedMessages = currentUIState.feedMessages.map { message ->
                    if (message.id == messageId) {
                        message.copy(
                            attributes = message.attributes.apply { update(this) }
                        )
                    } else {
                        message
                    }
                }

                _state.value = _state.value.copy(
                    uiState = currentUIState.copy(feedMessages = updatedMessages)
                )
            }
            is FeedUIState.Replying -> {
                val updatedMessages = currentUIState.feedMessages.map { message ->
                    if (message.id == messageId) {
                        message.copy(
                            attributes = message.attributes.apply { update(this) }
                        )
                    } else {
                        message
                    }
                }

                _state.value = _state.value.copy(
                    uiState = currentUIState.copy(feedMessages = updatedMessages)
                )
            }
            is FeedUIState.Editing -> {
                val updatedMessages = currentUIState.feedMessages.map { message ->
                    if (message.id == messageId) {
                        message.copy(
                            attributes = message.attributes.apply { update(this) }
                        )
                    } else {
                        message
                    }
                }

                _state.value = _state.value.copy(
                    uiState = currentUIState.copy(feedMessages = updatedMessages)
                )
            }
            else -> {}
        }
    }
}
