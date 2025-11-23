package app.kurozora.ui.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kurozorakit.core.KurozoraKit
import kurozorakit.data.models.feed.message.FeedMessage
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
            _state.value = _state.value.copy(isLoading = true)
            when (val result = kurozoraKit.feed().getExploreFeed()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        feedMessages = result.data.data,
                        nextPageUrl = result.data.next,
                        error = null
                    )
                }

                is Result.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.error.message
                    )
                }
            }
        }
    }

    fun loadMore() {
        val next = _state.value.nextPageUrl ?: return
        viewModelScope.launch {
            when (val result = kurozoraKit.feed().getExploreFeed(next = next)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        feedMessages = state.value.feedMessages + result.data.data,
                        nextPageUrl = result.data.next
                    )
                }

                else -> {}
            }
        }
    }

    fun heartMessage(message: FeedMessage) {
        viewModelScope.launch {
            kurozoraKit.feed().heartFeedMessage(message.id)
        }
    }

    fun reshareMessage(message: FeedMessage) {
        viewModelScope.launch {
            kurozoraKit.feed().pinFeedMessage(message.id)
        }
    }
}
